package com.mygdx.hadal.event;

import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.schmucks.bodies.HadalEntity;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity.particleSyncType;
import com.mygdx.hadal.server.Packets;
import com.mygdx.hadal.states.ClientState;
import com.mygdx.hadal.states.ClientState.ObjectSyncLayers;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
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
public class Poison extends Event {
	
	private float controllerCount = 0;
	
	//Damage done by the poison
	private float dps;
	
	//If created by an dude, this is that dude
	private Schmuck perp;
	
	//Is the poison on? Should it be drawn? Should random particles be spawned in its vicinity?
	private boolean on, draw, randomParticles;

	private float currPoisonSpawnTimer = 0f, spawnTimerLimit;
	private short filter;
	
	private final static float damageInterval = 1 / 60f;
	
	public Poison(PlayState state, Vector2 startPos, Vector2 size, float dps, boolean draw, short filter) {
		super(state,  startPos, size);
		this.dps = dps;
		this.filter = filter;
		this.perp = state.getWorldDummy();
		this.draw = draw;
		this.on = true;
		
		spawnTimerLimit = 4096f / (size.x * size.y);
		
		randomParticles = size.x > 100;
		
		if (!randomParticles && draw) {
			new ParticleEntity(state, this, Particle.POISON, 0, 0, on, particleSyncType.CREATESYNC);
		}
	}
	
	/**
	 * This constructor is used for when this event is created temporarily.
	 */
	public Poison(PlayState state, Vector2 startPos, Vector2 size, float dps, float duration, Schmuck perp, boolean draw, short filter) {
		super(state,  startPos, size, duration);
		this.dps = dps;
		this.filter = filter;
		this.perp = perp;
		this.draw = draw;
		this.on = true;
		spawnTimerLimit = 4096f/(size.x * size.y);
		
		randomParticles = size.x > 100;
		
		if (!randomParticles && draw) {
			new ParticleEntity(state, this, Particle.POISON, 1.5f, 0, on, particleSyncType.CREATESYNC);
		}
	}
	
	@Override
	public void create() {

		this.eventData = new EventData(this) {
			
			@Override
			public void onActivate(EventData activator, Player p) {
				on = !on;
			}
		};
		
		this.body = BodyBuilder.createBox(world, startPos, size, 0, 0, 0, false, false, Constants.BIT_SENSOR, (short) (Constants.BIT_PLAYER | Constants.BIT_ENEMY), filter, true, eventData);
	}
	
	@Override
	public void controller(float delta) {
		if (on) {
			controllerCount += delta;
			while (controllerCount >= damageInterval) {
				controllerCount -= damageInterval;
				
				for (HadalEntity entity : eventData.getSchmucks()) {
					if (entity instanceof Schmuck) {
						((Schmuck)entity).getBodyData().receiveDamage(dps, new Vector2(), perp.getBodyData(), true, DamageTypes.POISON);
					}
				}
			}
			
			//if specified, spawn random posion particles in the event's vicinity
			if (randomParticles && draw) {
				currPoisonSpawnTimer += delta;
				while (currPoisonSpawnTimer >= spawnTimerLimit) {
					currPoisonSpawnTimer -= spawnTimerLimit;
					int randX = (int) ((Math.random() * size.x) - (size.x / 2) + getPixelPosition().x);
					int randY = (int) ((Math.random() * size.y) - (size.y / 2) + getPixelPosition().y);
					new ParticleEntity(state, new Vector2(randX, randY), Particle.POISON, 1.5f, true, particleSyncType.NOSYNC);
				}
			}
		}
		super.controller(delta);
	}
	
	/**
	 * Client Poison should randomly spawn poison particles itself to avoid overhead.
	 */
	@Override
	public void clientController(float delta) {
		if (on) {
			if (randomParticles && draw) {
				currPoisonSpawnTimer += delta;
				while (currPoisonSpawnTimer >= spawnTimerLimit) {
					currPoisonSpawnTimer -= spawnTimerLimit;
					int randX = (int) ((Math.random() * size.x) - (size.x / 2) + getPixelPosition().x);
					int randY = (int) ((Math.random() * size.y) - (size.y / 2) + getPixelPosition().y);
					ParticleEntity poison = new ParticleEntity(state, new Vector2(randX, randY), Particle.POISON, 1.5f, true, particleSyncType.NOSYNC);
					((ClientState)state).addEntity(poison.getEntityID().toString(), poison, ObjectSyncLayers.STANDARD);
				}
			}
		}
	}
	
	/**
	 * When server creates poison, clients are told to create the poison in their own worlds
	 */
	@Override
	public Object onServerCreate() {
		if (blueprint == null) {
			blueprint = new RectangleMapObject(getPixelPosition().x - size.x / 2, getPixelPosition().y - size.y / 2, size.x, size.y);
			blueprint.setName("Poison");
			return new Packets.CreateEvent(entityID.toString(), blueprint);
		} else {
			return new Packets.CreateEvent(entityID.toString(), blueprint);
		}
	}
}
