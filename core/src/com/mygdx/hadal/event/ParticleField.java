package com.mygdx.hadal.event;

import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity.particleSyncType;
import com.mygdx.hadal.server.Packets;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.b2d.BodyBuilder;

/**
 * This event damages all schmucks inside of it. It can be spawned as a hazard in a map or created temporarily fro mthe effects 
 * of attacks
 * 
 * Triggered Behavior: Toggle whether the poison is on or off
 * Triggering Behavior: N/A
 * 
 * Fields:
 * damage: float damage per 1/60f done by this event
 * startOn: boolean of whether this event starts on or off. Optional. Default: true.
 * 
 * @author Zachary Tu
 *
 */
public class ParticleField extends Event {
	
	private Particle particle;
	private float duration;
	private float currParticleSpawnTimer = 0f, spawnTimerLimit;
	
	public ParticleField(PlayState state, Vector2 startPos, Vector2 size, Particle particle, float speed, float duration) {
		super(state, startPos, size);
		this.particle = particle;
		this.duration = duration;
		spawnTimerLimit = 4096f / (size.x * size.y) / speed;
	}
	
	@Override
	public void create() {

		this.eventData = new EventData(this);
		
		this.body = BodyBuilder.createBox(world, startPos, size, 0, 0, 0, false, false, Constants.BIT_SENSOR, (short) (Constants.BIT_PLAYER | Constants.BIT_ENEMY), (short)0, true, eventData);
	}
	
	@Override
	public void controller(float delta) {
		
		//if specified, spawn random posion particles in the event's vicinity
		currParticleSpawnTimer += delta;
		while (currParticleSpawnTimer >= spawnTimerLimit) {
			currParticleSpawnTimer -= spawnTimerLimit;
			int randX = (int) ((Math.random() * size.x) - (size.x / 2) + getPixelPosition().x);
			int randY = (int) ((Math.random() * size.y) - (size.y / 2) + getPixelPosition().y);
			new ParticleEntity(state, new Vector2(randX, randY), particle, duration, true, particleSyncType.NOSYNC);
		}
	}
	
	/**
	 * Client Poison should randomly spawn poison particles itself to avoid overhead.
	 */
	@Override
	public void clientController(float delta) {
		currParticleSpawnTimer += delta;
		while (currParticleSpawnTimer >= spawnTimerLimit) {
			currParticleSpawnTimer -= spawnTimerLimit;
			int randX = (int) ((Math.random() * size.x) - (size.x / 2) + getPixelPosition().x);
			int randY = (int) ((Math.random() * size.y) - (size.y / 2) + getPixelPosition().y);
			new ParticleEntity(state, new Vector2(randX, randY), particle, duration, true, particleSyncType.NOSYNC);
		}
	}
	
	/**
	 * When server creates poison, clients are told to create the poison in their own worlds
	 */
	@Override
	public Object onServerCreate() {
		if (blueprint == null) {
			blueprint = new RectangleMapObject(getPixelPosition().x - size.x / 2, getPixelPosition().y - size.y / 2, size.x, size.y);
			blueprint.setName("ParticleField");
			blueprint.getProperties().put("particle", particle.name());
			return new Packets.CreateEvent(entityID.toString(), blueprint);
		} else {
			return new Packets.CreateEvent(entityID.toString(), blueprint);
		}
	}
}
