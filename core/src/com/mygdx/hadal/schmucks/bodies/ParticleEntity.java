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
	private boolean despawn, temp, on;
	
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
		this.linger = linger;
		
		if (attachedEntity != null) {
			if (attachedEntity.isAlive() && attachedEntity.getBody() != null) {
				this.effect.setPosition(attachedEntity.getBody().getPosition().x * PPM, attachedEntity.getBody().getPosition().y * PPM);
			}
		}
	}

	@Override
	public void create() {
		
	}

	@Override
	public void controller(float delta) {
		if (attachedEntity != null && !despawn) {
			if (attachedEntity.isAlive() && attachedEntity.getBody() != null) {
				effect.setPosition(attachedEntity.getBody().getPosition().x * PPM, attachedEntity.getBody().getPosition().y * PPM);
			} else {
				despawn = true;
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

	@Override
	public void clientController(float delta) {
		controller(delta);
		if (attachedEntity == null && attachedId != null) {
			attachedEntity = ((ClientState)state).findEntity(attachedId);
		}
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
		batch.setProjectionMatrix(state.sprite.combined);
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
	
	@Override
	public void onServerSync() {
		if (sync.equals(particleSyncType.TICKSYNC)) {
			HadalGame.server.server.sendToAllUDP(new Packets.SyncParticles(entityID.toString(), on));
		}
	}
	
	@Override
	public void onClientSync(Object o) {
		Packets.SyncParticles p = (Packets.SyncParticles) o;
		
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
