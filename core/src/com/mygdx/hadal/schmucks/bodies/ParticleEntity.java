package com.mygdx.hadal.schmucks.bodies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool.PooledEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.server.Packets;
import com.mygdx.hadal.states.ClientState;
import com.mygdx.hadal.states.PlayState;

/**
 * The particle entity is an invisible, ephemeral entity that emits particle effects.
 * Atm, this is needed so that other entities can have particle effects that persist beyond their own disposal.
 * @author Zachary Tu
 *
 */
public class ParticleEntity extends HadalEntity {

	//What particles come out of this entity?
	private PooledEffect effect;
	private Particle particle;
	
	//Is this entity following another entity?
	private HadalEntity attachedEntity;
	private String attachedId;
	
	//How long this entity will last after deletion.
	private float linger, interval, lifespan;
	
	//Has the attached entity despawned yet?
	private boolean despawn;
	
	//Will the particle despawn after a duration?
	private boolean temp;
	
	//Does the particle despawn when its attached entity dies?
	private boolean attached;
	
	//Is the particle currently on?
	private boolean on;
	
	//how is this entity synced?
	private particleSyncType sync;
	
	//size multiplier of the particles
	private float scale = 1.0f;
	
	//if attached to an entity, this vector is the offset of the particle from the attached entity's location
	private Vector2 offset = new Vector2();
	
	//This constructor creates a particle effect at an area.
	public ParticleEntity(PlayState state, Vector2 startPos, Particle particle, float lifespan, boolean startOn, particleSyncType sync) {
		super(state, startPos, new Vector2());
		this.particle = particle;
		this.effect = particle.getParticle();
		this.on = startOn;
		this.sync = sync;
		this.despawn = false;
		
		temp = lifespan != 0;
		this.lifespan = lifespan;
		
		if (startOn) {
			this.effect.start();
		} else {
			this.effect.allowCompletion();
		}
		this.effect.setPosition(startPos.x, startPos.y);
	}
	
	//This constructor creates a particle effect that will follow another entity.
	public ParticleEntity(PlayState state, HadalEntity entity, Particle particle, float linger, float lifespan, boolean startOn, particleSyncType sync) {
		this(state, new Vector2(), particle, lifespan, startOn, sync);
		this.attachedEntity = entity;
		attached = linger == 0;
		this.linger = linger;
		
		if (attachedEntity != null) {
			if (attachedEntity.isAlive() && attachedEntity.getBody() != null) {
				this.effect.setPosition(attachedEntity.getPixelPosition().x, attachedEntity.getPixelPosition().y);
			} else {
				this.effect.setPosition(attachedEntity.getStartPos().x, attachedEntity.getStartPos().y);
			}
		}
	}

	//this constructor is used for particles with an offset from their attached entity
	public ParticleEntity(PlayState state, HadalEntity entity, Particle particle, float linger, float lifespan, boolean startOn, particleSyncType sync, Vector2 offset) {
		this(state, entity, particle, linger, lifespan, startOn, sync);
		this.offset.set(offset);
	}
	
	@Override
	public void create() {}

	@Override
	public void controller(float delta) {
		
		effect.update(delta);
		
		//If attached to a living unit, this entity tracks its movement. If attached to a unit that has died, we despawn.
		if (attachedEntity != null && !despawn) {
			if (attachedEntity.isAlive() && attachedEntity.getBody() != null) {
				
				effect.setPosition(attachedEntity.getPixelPosition().x + offset.x, attachedEntity.getPixelPosition().y + offset.y);
				
			} else {
				if (!attached) {
					despawn = true;
				}
				turnOff();
			}
		}
		
		//if despawned, we delete this entity after its lingering period
		if (despawn) {
			linger -= delta;
			
			if (linger <= 0) {
				this.queueDeletion();
			}
		}

		//particles with a timer are deleting when the timer runs out. Clients remove these too if they are processing them independently from the server.
		if (temp) {
			lifespan -= delta;
			if (lifespan <= 0) {
				if (state.isServer()) {
					this.queueDeletion();
				} else {
					((ClientState) state).removeEntity(entityID.toString());
				}
			}
		}
		
		//particles that are turned on for a timed period turn off when the interval is over
		if (interval > 0) {
			interval -= delta;
			
			if (interval <= 0) {
				turnOff();
			}
		}
	}

	/**
	 * Client ParticleEntites will run normally if set to Create or No Sync
	 * If attached to an entity that hasn't been sent over yet, wait until it exists and then attach
	 */
	@Override
	public void clientController(float delta) {
		
		//client particles process independently from the server if they are set to CREATESYNC or NOSYNC
		if (sync.equals(particleSyncType.CREATESYNC) || sync.equals(particleSyncType.NOSYNC)) {
			controller(delta);			
		}
		
		//client particles are sometimes told to attach to a unit that the client hasn't created yet. This code makes the particle entity wait for its attached entity to be created
		if (attachedEntity == null && attachedId != null) {
			attachedEntity = ((ClientState) state).findEntity(attachedId);
		}
	}
	
	/**
	 * Is this entity on the screen?
	 * use particle bounding box to calculate
	 */
	@Override
	public boolean isVisible() {
		return true;
	}

	public void turnOn() {
		on = true;
		effect.start();
	}
	
	public void turnOff() {
		on = false;
		effect.allowCompletion();
	}

	/**
	 * This turns the particle on for an input period of time
	 */
	public void onForBurst(float duration) {
		turnOn();
		interval = duration;
	}
	
	@Override
	public void render(SpriteBatch batch) {
		effect.draw(batch, Gdx.graphics.getDeltaTime());
	}
	
	@Override
	public void dispose() {		
		
		if (alive) {
			effect.reset();
			effect.free();
		}
		super.dispose();
	}

	/**
	 * When created on the server, tell clients to create if create or tick sync.
	 */
	@Override
	public Object onServerCreate() {
		if (sync.equals(particleSyncType.CREATESYNC) || sync.equals(particleSyncType.TICKSYNC)) {
			if (attachedEntity != null) {
				return new Packets.CreateParticles(entityID.toString(), attachedEntity.getEntityID().toString(), offset, true, particle.toString(), on, linger, lifespan, scale);
			} else {
				return new Packets.CreateParticles(entityID.toString(), null, new Vector2(startPos), false, particle.toString(), on, linger, lifespan, scale);
			}
		} else {
			return null;
		}
	}
	
	/**
	 * For particles that are tick synced, send over location to clients as well as whether it is on or not
	 */
	private Vector2 newPos = new Vector2();
	@Override
	public void onServerSync() {
		
		if (sync.equals(particleSyncType.TICKSYNC)) {
			if (attachedEntity != null) {
				if (attachedEntity.getBody() != null) {
					newPos.set(attachedEntity.getPixelPosition().x, attachedEntity.getPixelPosition().y);
					HadalGame.server.sendToAllUDP(new Packets.SyncParticles(entityID.toString(), newPos, offset, on));
				} else {
					newPos.set(startPos);
					HadalGame.server.sendToAllUDP(new Packets.SyncParticles(entityID.toString(), newPos, offset, on));
				}
			} else {
				newPos.set(startPos);
				HadalGame.server.sendToAllUDP(new Packets.SyncParticles(entityID.toString(), newPos, offset, on));
			}
		}
	}
	
	/**
	 * For Client Particle entities, sync position and on if the server sends over the packets (if Tick synced)
	 */
	@Override
	public void onClientSync(Object o) {
		Packets.SyncParticles p = (Packets.SyncParticles) o;
		this.offset.set(p.offset);
		effect.setPosition(p.pos.x + offset.x, p.pos.y + offset.y);

		if (p.on && (!on || effect.isComplete())) {
			turnOn();
		}
		if (!p.on && (on || !effect.isComplete())) {
			turnOff();
		}
	}
	
	/**
	 * This sets the scale of the particle
	 */
	public void setScale(float scale) { 
		this.scale = scale;
		this.effect.scaleEffect(scale);
	}

	public void setAttachedEntity(HadalEntity attachedEntity) { this.attachedEntity = attachedEntity; }
	
	public void setDespawn(boolean despawn) { this.despawn = despawn; }
	
	public void setAttachedId(String attachedId) { this.attachedId = attachedId; }

	public void setOffset(Vector2 offset) { this.offset.set(offset); }
	
	public enum particleSyncType {
		NOSYNC,
		CREATESYNC,
		TICKSYNC
	}
}
