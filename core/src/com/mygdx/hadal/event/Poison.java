package com.mygdx.hadal.event;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.constants.BodyConstants;
import com.mygdx.hadal.constants.Constants;
import com.mygdx.hadal.constants.SyncType;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.schmucks.entities.HadalEntity;
import com.mygdx.hadal.schmucks.entities.ParticleEntity;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.states.PlayStateClient;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.states.PlayState.ObjectLayer;
import com.mygdx.hadal.utils.b2d.HadalBody;

/**
 * This event damages all schmucks inside of it. It can be spawned as a hazard in a map or created temporarily from the effects
 * of attacks
 * <p>
 * Triggered Behavior: Toggle whether the poison is on or off
 * Triggering Behavior: N/A
 * <p>
 * Fields:
 * damage: float damage per 1/60f done by this event
 * draw: do we draw particles for this event? Default: true
 * filter: who does this event affect? Default: 0 (all units)
 * @author Clostafa Clurbara
 */
public class Poison extends Event {
	
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

	private float particleLifespan = 1.5f;
	private float particleInterval = 4096f;

	private Particle poisonParticle;

	private DamageSource source = DamageSource.MAP_POISON;

	public Poison(PlayState state, Vector2 startPos, Vector2 size, String particle, float dps, boolean draw, short filter) {
		super(state,  startPos, size);
		this.poisonParticle = Particle.valueOf(particle);
		this.dps = dps;
		this.filter = filter;
		this.perp = state.getWorldDummy();
		this.draw = draw;
		this.on = true;
		
		spawnTimerLimit = particleInterval / (size.x * size.y);
		
		randomParticles = size.x > 100;
		
		if (!randomParticles && draw) {
			ParticleEntity particleEntity = new ParticleEntity(state, this, poisonParticle, 0, 0,
					true, SyncType.NOSYNC);
			if (!state.isServer()) {
				((PlayStateClient) state).addEntity(particleEntity.getEntityID(), particleEntity, false, ObjectLayer.EFFECT);
			}
		}
	}

	public Poison(PlayState state, Vector2 startPos, Vector2 size, float dps, float duration, Schmuck perp, boolean draw,
				  short filter, DamageSource source) {
		this(state, startPos, size, "POISON", dps, duration, perp, draw, filter, source);

		setIndependent(true);
	}
	/**
	 * This constructor is used for when this event is created temporarily.
	 */
	public Poison(PlayState state, Vector2 startPos, Vector2 size, String particle, float dps, float duration, Schmuck perp,
				  boolean draw, short filter, DamageSource source) {
		super(state,  startPos, size, duration);
		this.poisonParticle = Particle.valueOf(particle);
		this.dps = dps;
		this.filter = filter;
		this.source = source;
		
		if (perp == null) {
			this.perp = state.getWorldDummy();
		} else {
			this.perp = perp;

			//this prevents team killing using poison in the hub
			if (perp.getHitboxFilter() == BodyConstants.PLAYER_HITBOX && !state.getMode().isFriendlyFire()) {
				this.filter = BodyConstants.PLAYER_HITBOX;
			}
		}

		this.draw = draw;
		this.on = true;
		spawnTimerLimit = particleInterval / (size.x * size.y);
		
		randomParticles = size.x > 100;
		
		if (!randomParticles && draw) {
			ParticleEntity particleEntity = new ParticleEntity(state, this, poisonParticle, particleLifespan, 0,
					true, SyncType.NOSYNC);
			if (!state.isServer()) {
				((PlayStateClient) state).addEntity(particleEntity.getEntityID(), particleEntity, false, ObjectLayer.EFFECT);
			}
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

		this.body = new HadalBody(eventData, startPos, size, BodyConstants.BIT_SENSOR,
				(short) (BodyConstants.BIT_PLAYER | BodyConstants.BIT_ENEMY), filter)
				.addToWorld(world);
	}

	private float controllerCount;
	private final Vector2 entityLocation = new Vector2();
	private final Vector2 randLocation = new Vector2();
	@Override
	public void controller(float delta) {
		if (on) {
			super.controller(delta);

			controllerCount += delta;
			while (controllerCount >= Constants.INTERVAL) {
				controllerCount -= Constants.INTERVAL;
				
				for (HadalEntity entity : eventData.getSchmucks()) {
					if (entity instanceof Schmuck schmuck) {
						schmuck.getBodyData().receiveDamage(dps, new Vector2(), perp.getBodyData(), true,
								null, source, DamageTag.POISON);
					}
				}
			}
			
			//if specified, spawn random poison particles in the event's vicinity
			if (randomParticles && draw) {
				
				entityLocation.set(getPixelPosition());
				
				currPoisonSpawnTimer += delta;
				while (currPoisonSpawnTimer >= spawnTimerLimit) {
					currPoisonSpawnTimer -= spawnTimerLimit;
					int randX = (int) ((MathUtils.random() * size.x) - (size.x / 2) + entityLocation.x);
					int randY = (int) ((MathUtils.random() * size.y) - (size.y / 2) + entityLocation.y);
					new ParticleEntity(state, randLocation.set(randX, randY), poisonParticle, particleLifespan, true, SyncType.NOSYNC);
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

			controllerCount += delta;
			while (controllerCount >= Constants.INTERVAL) {
				controllerCount -= Constants.INTERVAL;

				for (HadalEntity entity : eventData.getSchmucks()) {
					if (entity instanceof Schmuck schmuck) {
						schmuck.getBodyData().receiveDamage(dps, new Vector2(), perp.getBodyData(), true,
								null, source, DamageTag.POISON);
					}
				}
			}

			if (randomParticles && draw) {
				
				entityLocation.set(getPixelPosition());
				
				currPoisonSpawnTimer += delta;
				while (currPoisonSpawnTimer >= spawnTimerLimit) {
					currPoisonSpawnTimer -= spawnTimerLimit;
					int randX = (int) ((MathUtils.random() * size.x) - (size.x / 2) + entityLocation.x);
					int randY = (int) ((MathUtils.random() * size.y) - (size.y / 2) + entityLocation.y);
					ParticleEntity poison = new ParticleEntity(state, randLocation.set(randX, randY), poisonParticle,
							particleLifespan, true, SyncType.NOSYNC);
					((PlayStateClient) state).addEntity(poison.getEntityID(), poison, false, ObjectLayer.EFFECT);
				}
			}
		}
	}

	public Poison setParticle(Particle particle) {
		this.poisonParticle = particle;
		return this;
	}

	public Poison setParticleLifespan(float particleLifespan) {
		this.particleLifespan = particleLifespan;
		return this;
	}

	public Poison setParticleInterval(float particleInterval) {
		this.particleInterval = particleInterval;
		return this;
	}
}
