package com.mygdx.hadal.event;

import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.schmucks.bodies.HadalEntity;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity.particleSyncType;
import com.mygdx.hadal.server.Packets;
import com.mygdx.hadal.schmucks.bodies.Ragdoll;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.b2d.BodyBuilder;

/**
 * Currents are an event that apply a continuous force to all schmucks inside of it.
 * 
 * Triggered Behavior: N/A
 * Triggering Behavior: N/A
 * 
 * Fields:
 * N/A
 * 
 * @author Zachary Tu
 *
 */
public class Currents extends Event {
	
	//force applied every 1/60 seconds
	private Vector2 vec;

	//This keeps track of engine timer.
	private float controllerCount = 0;
	
	private float currBubbleSpawnTimer = 0f, spawnTimerLimit;
	
	private final static float pushInterval = 1 / 60f;
	
	public Currents(PlayState state, Vector2 startPos, Vector2 size, Vector2 vec) {
		super(state, startPos, size);
		this.vec = vec;
		
		spawnTimerLimit = 4096f/(size.x * size.y);
	}
	
	public Currents(PlayState state, Vector2 startPos, Vector2 size, Vector2 vec, float duration) {
		super(state, startPos, size, duration);
		this.vec = vec;
		
		spawnTimerLimit = 2048f/(size.x * size.y);
	}
	
	@Override
	public void create() {

		this.eventData = new EventData(this);
		
		this.body = BodyBuilder.createBox(world, startPos, size, 1, 1, 0, true, true, Constants.BIT_SENSOR, 
				(short) (Constants.BIT_PLAYER | Constants.BIT_ENEMY | Constants.BIT_PROJECTILE | Constants.BIT_SENSOR),
				(short) 0, true, eventData);
	}
	
	@Override
	public void controller(float delta) {
		super.controller(delta);
		
		controllerCount += delta;
		while (controllerCount >= pushInterval) {
			controllerCount -= pushInterval;
			
			for (HadalEntity entity : eventData.getSchmucks()) {
				entity.applyLinearImpulse(vec);
			}
		}
		
		currBubbleSpawnTimer += delta;
		while (currBubbleSpawnTimer >= spawnTimerLimit) {
			currBubbleSpawnTimer -= spawnTimerLimit;
			int randX = (int) ((Math.random() * size.x) - (size.x / 2) + getPixelPosition().x);
			int randY = (int) ((Math.random() * size.y) - (size.y / 2) + getPixelPosition().y);
			new ParticleEntity(state, new Ragdoll(state, new Vector2(randX, randY), new Vector2(64, 64), Sprite.NOTHING, new Vector2(0, 0), 0.5f, true),
					Particle.BUBBLE_TRAIL, 0.5f, 0.0f, true, particleSyncType.CREATESYNC);
		}
	}
	
	/**
	 * When server creates poison, clients are told to create the poison in their own worlds
	 */
	@Override
	public Object onServerCreate() {
		if (blueprint == null) {
			blueprint = new RectangleMapObject(getPixelPosition().x - size.x / 2, getPixelPosition().y - size.y / 2, size.x, size.y);
			blueprint.setName("Current");
			blueprint.getProperties().put("currentX", vec.x);
			blueprint.getProperties().put("currentY", vec.y);
			return new Packets.CreateEvent(entityID.toString(), blueprint);
		} else {
			return new Packets.CreateEvent(entityID.toString(), blueprint);
		}
	}
}
