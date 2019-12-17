package com.mygdx.hadal.schmucks.bodies;

import static com.mygdx.hadal.utils.Constants.PPM;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
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
	private ParticleEffect effect;
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
	
	private particleSyncType sync;
	
	//This constructor creates a particle effect at an area.
	public ParticleEntity(PlayState state, float startX, float startY, Particle particle, float lifespan, boolean startOn, particleSyncType sync) {
		super(state, 0, 0, startX, startY);
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
		
		this.effect.setPosition(startX, startY);		
	}
	
	//This constructor creates a particle effect that will follow another entity.
	public ParticleEntity(PlayState state, HadalEntity entity, Particle particle, float linger, float lifespan, boolean startOn, particleSyncType sync) {
		this(state, 0, 0, particle, lifespan, startOn, sync);
		this.attachedEntity = entity;
		attached = linger == 0;
		this.linger = linger;
		
		if (attachedEntity != null) {
			if (attachedEntity.isAlive() && attachedEntity.getBody() != null) {
				this.effect.setPosition(attachedEntity.getPosition().x * PPM, attachedEntity.getPosition().y * PPM);
			}
		}
	}

	@Override
	public void create() {}

	@Override
	public void controller(float delta) {
		if (attachedEntity != null && !despawn) {
			if (attachedEntity.isAlive() && attachedEntity.getBody() != null) {
				effect.setPosition(attachedEntity.getPosition().x * PPM, attachedEntity.getPosition().y * PPM);
			} else {
				if (!attached) {
					despawn = true;
				}
				turnOff();
			}
		}
		
		if (despawn) {
			linger -= delta;
			
			if (linger <= 0) {
				this.queueDeletion();
			}
		}

		if (temp) {
			lifespan -= delta;
			
			if (lifespan <= 0) {
				if (state.isServer()) {
					this.queueDeletion();
				} else {
					((ClientState)state).removeEntity(entityID.toString());
				}
			}
		}
		
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
		if (sync.equals(particleSyncType.CREATESYNC) || sync.equals(particleSyncType.NOSYNC)) {
			controller(delta);			
		}
		if (attachedEntity == null && attachedId != null) {
			attachedEntity = ((ClientState)state).findEntity(attachedId);
		}
	}
	
	
	/**
	 * Is this entity on the screen?
	 * @return
	 */
	@Override
	public boolean isVisible() {
		return camera.frustum.boundsInFrustum(effect.getBoundingBox());
	}
	
	public void turnOn() {
		on = true;
		effect.start();
	}
	
	public void turnOff() {
		on = false;
		effect.allowCompletion();
	}

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
		effect.dispose();
		super.dispose();
	}

	public ParticleEffect getEffect() {
		return effect;
	}

	public void setEffect(ParticleEffect effect) {
		this.effect = effect;
	}

	public HadalEntity getAttachedEntity() {
		return attachedEntity;
	}

	public void setAttachedEntity(HadalEntity attachedEntity) {
		this.attachedEntity = attachedEntity;
	}
	
	public void setDespawn(boolean despawn) {
		this.despawn = despawn;
	}

	/**
	 * When created on the server, tell clients to create if create or tick sync.
	 */
	@Override
	public Object onServerCreate() {
		
		if (sync.equals(particleSyncType.CREATESYNC) || sync.equals(particleSyncType.TICKSYNC)) {
			if (attachedEntity != null) {
				return new Packets.CreateParticles(entityID.toString(), attachedEntity.getEntityID().toString(), new Vector2(0,0), 
						true, particle.toString(), on, linger, lifespan);
			} else {
				return new Packets.CreateParticles(entityID.toString(), null, new Vector2(startX, startY), 
						false, particle.toString(), on, linger, lifespan);
			}
		} else {
			return null;
		}
	}
	
	/**
	 * For particles that are tick synced, send over location to clients as well as whether it is on or not
	 */
	@Override
	public void onServerSync() {
		if (sync.equals(particleSyncType.TICKSYNC)) {
			if (attachedEntity != null) {
				if (attachedEntity.getBody() != null) {
					Vector2 newPos = new Vector2(
							attachedEntity.getPosition().x * PPM, 
							attachedEntity.getPosition().y * PPM);
					HadalGame.server.sendToAllUDP(new Packets.SyncParticles(entityID.toString(), newPos, on));
				}
			} else {
				HadalGame.server.sendToAllUDP(new Packets.SyncParticles(entityID.toString(), new Vector2(startX, startY), on));
			}
		}
	}
	
	/**
	 * For Client Particle entities, sync position and on if the server sends over the packes (if Tick synced)
	 */
	@Override
	public void onClientSync(Object o) {
		Packets.SyncParticles p = (Packets.SyncParticles) o;
		effect.setPosition(p.pos.x, p.pos.y);

		if (p.on && (!on || effect.isComplete())) {
			turnOn();
		}
		if (!p.on && (on || !effect.isComplete())) {
			turnOff();
		}
	}

	public void setAttachedId(String attachedId) {
		this.attachedId = attachedId;
	}

	public enum particleSyncType {
		NOSYNC,
		CREATESYNC,
		TICKSYNC
	}
}
