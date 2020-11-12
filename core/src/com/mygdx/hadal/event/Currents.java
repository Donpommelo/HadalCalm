package com.mygdx.hadal.event;

import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.schmucks.bodies.HadalEntity;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity.particleSyncType;
import com.mygdx.hadal.server.EventDto;
import com.mygdx.hadal.server.Packets;
import com.mygdx.hadal.schmucks.bodies.Ragdoll;
import com.mygdx.hadal.states.ClientState;
import com.mygdx.hadal.states.ClientState.ObjectSyncLayers;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
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
 * @author Frularbus Fortrand
 */
public class Currents extends Event {
	
	//force applied every 1/60 seconds
	private final Vector2 vec = new Vector2();

	//This keeps track of engine timer.
	private float controllerCount;
	
	//these keep track of spawned particle dummies inside the current
	private float currBubbleSpawnTimer;
	private final float spawnTimerLimit;
	
	private static final float pushInterval = 1 / 60f;
	
	public Currents(PlayState state, Vector2 startPos, Vector2 size, Vector2 vec) {
		super(state, startPos, size);
		this.vec.set(vec);
		
		spawnTimerLimit = 5120 / (size.x * size.y);
	}
	
	public Currents(PlayState state, Vector2 startPos, Vector2 size, Vector2 vec, float duration) {
		super(state, startPos, size, duration);
		this.vec.set(vec);
		
		spawnTimerLimit = 2560 / (size.x * size.y);
	}
	
	@Override
	public void create() {

		this.eventData = new EventData(this);
		
		this.body = BodyBuilder.createBox(world, startPos, size, 1, 1, 0, true, true, Constants.BIT_SENSOR, 
				(short) (Constants.BIT_PLAYER | Constants.BIT_ENEMY | Constants.BIT_PROJECTILE | Constants.BIT_SENSOR),
				(short) 0, true, eventData);
	}
	
	private final Vector2 entityLocation = new Vector2();
	private final Vector2 randLocation = new Vector2();
	private final Vector2 ragdollSize = new Vector2(48, 48);
	private final Vector2 ragdollVelo = new Vector2();
	@Override
	public void controller(float delta) {
		super.controller(delta);
		
		controllerCount += delta;
		while (controllerCount >= pushInterval) {
			controllerCount -= pushInterval;
			
			//push is done through damage so that +knockback resistance will reduce the push.
			for (HadalEntity entity : eventData.getSchmucks()) {
				entity.getHadalData().receiveDamage(0.0f, new Vector2(vec), state.getWorldDummy().getBodyData(), false, DamageTypes.DEFLECT);
			}
		}
		
		entityLocation.set(getPixelPosition());
		
		//spawn a dummy with a particle attached. Dummies are moved by current to give visual effect.
		currBubbleSpawnTimer += delta;
		while (currBubbleSpawnTimer >= spawnTimerLimit) {
			currBubbleSpawnTimer -= spawnTimerLimit;
			int randX = (int) ((Math.random() * size.x) - (size.x / 2) + entityLocation.x);
			int randY = (int) ((Math.random() * size.y) - (size.y / 2) + entityLocation.y);
			new ParticleEntity(state, new Ragdoll(state, randLocation.set(randX, randY), ragdollSize, Sprite.NOTHING, ragdollVelo, 0.25f, 0.0f, false, true, false),
					Particle.BUBBLE_TRAIL, 0.5f, 0.0f, true, particleSyncType.NOSYNC);
		}
	}
	
	@Override
	public void clientController(float delta) {
		super.controller(delta);
		
		controllerCount += delta;
		while (controllerCount >= pushInterval) {
			controllerCount -= pushInterval;
			
			//push is done through damage so that +knockback resistance will reduce the push.
			for (HadalEntity entity : eventData.getSchmucks()) {
				entity.getHadalData().receiveDamage(0.0f, randLocation.set(vec), state.getWorldDummy().getBodyData(), false, DamageTypes.DEFLECT);
			}
		}
		
		entityLocation.set(getPixelPosition());
		
		//spawn a dummy with a particle attached. Dummies are moved by current to give visual effect.
		currBubbleSpawnTimer += delta;
		while (currBubbleSpawnTimer >= spawnTimerLimit) {
			currBubbleSpawnTimer -= spawnTimerLimit;
			int randX = (int) ((Math.random() * size.x) - (size.x / 2) + entityLocation.x);
			int randY = (int) ((Math.random() * size.y) - (size.y / 2) + entityLocation.y);
			
			Ragdoll ragdoll = new Ragdoll(state, randLocation.set(randX, randY), ragdollSize, Sprite.NOTHING, ragdollVelo, 0.25f, 0.0f, false, true, false);
			ParticleEntity bubbles = new ParticleEntity(state, ragdoll, Particle.BUBBLE_TRAIL, 0.5f, 0.0f, true, particleSyncType.NOSYNC);
			((ClientState) state).addEntity(ragdoll.getEntityID().toString(), ragdoll, false, ObjectSyncLayers.STANDARD);
			((ClientState) state).addEntity(bubbles.getEntityID().toString(), bubbles, false, ObjectSyncLayers.STANDARD);
		}
	}
		
	/**
	 * When server creates current, clients are told to create the current in their own worlds
	 */
	@Override
	public Object onServerCreate() {

		if (independent) { return null; }

		if (blueprint == null) {
			entityLocation.set(getPixelPosition());

			blueprint = new RectangleMapObject(entityLocation.x - size.x / 2, entityLocation.y - size.y / 2, size.x, size.y);
			blueprint.setName("CurrentTemp");
			blueprint.getProperties().put("currentX", vec.x);
			blueprint.getProperties().put("currentY", vec.y);
			blueprint.getProperties().put("duration", duration);
		}
		return new Packets.CreateEvent(entityID.toString(), new EventDto(blueprint), synced);
	}
}
