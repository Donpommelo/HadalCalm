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
import com.mygdx.hadal.server.EventDto;
import com.mygdx.hadal.server.Packets;
import com.mygdx.hadal.states.ClientState;
import com.mygdx.hadal.states.ClientState.ObjectSyncLayers;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.b2d.BodyBuilder;

/**
 * This event damages all schmucks inside of it. It can be spawned as a hazard in a map or created temporarily from the effects
 * of attacks
 * 
 * Triggered Behavior: Toggle whether the poison is on or off
 * Triggering Behavior: N/A
 * 
 * Fields:
 * damage: float damage per 1/60f done by this event
 * draw: do we draw particles for this event? Default: true
 * filter: who does this event affect? Default: 0 (all units)
 * @author Clostafa Clurbara
 */
public class Poison extends Event {
	
	private float controllerCount = 0;
	
	//Damage done by the poison
	private final float dps;
	
	//If created by an dude, this is that dude
	private final Schmuck perp;
	
	//Is the poison on? Should it be drawn? Should random particles be spawned in its vicinity?
	private boolean on;
	private final boolean draw, randomParticles;

	private float currPoisonSpawnTimer;
	private final float spawnTimerLimit;
	private short filter;
	
	private static final float damageInterval = 1 / 60f;

	private Particle poisonParticle = Particle.POISON;

	public Poison(PlayState state, Vector2 startPos, Vector2 size, float dps, boolean draw, short filter) {
		super(state,  startPos, size);
		this.dps = dps;
		this.filter = filter;
		this.perp = state.getWorldDummy();
		this.draw = draw;
		this.on = true;
		
		spawnTimerLimit = 4096f / (size.x * size.y);
		
		randomParticles = size.x > 100;
		
		if (!randomParticles && draw && state.isServer()) {
			new ParticleEntity(state, this, poisonParticle, 0, 0, true, particleSyncType.CREATESYNC);
		}
	}
	
	/**
	 * This constructor is used for when this event is created temporarily.
	 */
	public Poison(PlayState state, Vector2 startPos, Vector2 size, float dps, float duration, Schmuck perp, boolean draw, short filter) {
		super(state,  startPos, size, duration);
		this.dps = dps;

		this.filter = filter;
		
		if (perp == null) {
			this.perp = state.getWorldDummy();
		} else {
			this.perp = perp;

			//this prevents team killing using poison in the hub
			if (perp.getHitboxfilter() == Constants.PLAYER_HITBOX && state.isHub()) {
				this.filter = Constants.PLAYER_HITBOX;
			}
		}

		this.draw = draw;
		this.on = true;
		spawnTimerLimit = 4096f / (size.x * size.y);
		
		randomParticles = size.x > 100;
		
		if (!randomParticles && draw && state.isServer()) {
			new ParticleEntity(state, this, poisonParticle, 1.5f, 0, true, particleSyncType.CREATESYNC);
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
	
	private final Vector2 entityLocation = new Vector2();
	private final Vector2 randLocation = new Vector2();
	@Override
	public void controller(float delta) {
		if (on) {
			super.controller(delta);

			controllerCount += delta;
			while (controllerCount >= damageInterval) {
				controllerCount -= damageInterval;
				
				for (HadalEntity entity : eventData.getSchmucks()) {
					if (entity instanceof Schmuck) {
						((Schmuck) entity).getBodyData().receiveDamage(dps, new Vector2(), perp.getBodyData(), true, DamageTypes.POISON);
					}
				}
			}
			
			//if specified, spawn random poison particles in the event's vicinity
			if (randomParticles && draw) {
				
				entityLocation.set(getPixelPosition());
				
				currPoisonSpawnTimer += delta;
				while (currPoisonSpawnTimer >= spawnTimerLimit) {
					currPoisonSpawnTimer -= spawnTimerLimit;
					int randX = (int) ((Math.random() * size.x) - (size.x / 2) + entityLocation.x);
					int randY = (int) ((Math.random() * size.y) - (size.y / 2) + entityLocation.y);
					new ParticleEntity(state, randLocation.set(randX, randY), poisonParticle, 1.5f, true, particleSyncType.NOSYNC);
				}
			}
		}
	}
	
	/**
	 * Client Poison should randomly spawn poison particles itself to reduce overhead.
	 */
	@Override
	public void clientController(float delta) {
		if (on) {
			super.controller(delta);
			
			if (randomParticles && draw) {
				
				entityLocation.set(getPixelPosition());
				
				currPoisonSpawnTimer += delta;
				while (currPoisonSpawnTimer >= spawnTimerLimit) {
					currPoisonSpawnTimer -= spawnTimerLimit;
					int randX = (int) ((Math.random() * size.x) - (size.x / 2) + entityLocation.x);
					int randY = (int) ((Math.random() * size.y) - (size.y / 2) + entityLocation.y);
					ParticleEntity poison = new ParticleEntity(state, randLocation.set(randX, randY), poisonParticle, 1.5f, true, particleSyncType.NOSYNC);
					((ClientState) state).addEntity(poison.getEntityID().toString(), poison, false, ObjectSyncLayers.STANDARD);
				}
			}
		}
	}
	
	/**
	 * When server creates poison, clients are told to create the poison in their own worlds
	 */
	@Override
	public Object onServerCreate() {

		if (independent) { return null; }

		if (blueprint == null) {
			entityLocation.set(getPixelPosition());
			
			blueprint = new RectangleMapObject(entityLocation.x - size.x / 2, entityLocation.y - size.y / 2, size.x, size.y);
			blueprint.setName("PoisonTemp");
			blueprint.getProperties().put("duration", duration);
		}
		return new Packets.CreateEvent(entityID.toString(), new EventDto(blueprint), synced);
	}

	public Poison setParticle(Particle particle) {
		this.poisonParticle = particle;
		return this;
	}
}
