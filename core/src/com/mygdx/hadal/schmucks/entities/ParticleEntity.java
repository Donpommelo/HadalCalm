package com.mygdx.hadal.schmucks.entities;

import com.badlogic.gdx.graphics.g2d.ParticleEffectPool.PooledEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter.ScaledNumericValue;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.mygdx.hadal.effects.HadalColor;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.schmucks.SyncType;
import com.mygdx.hadal.server.packets.Packets;
import com.mygdx.hadal.server.packets.PacketsSync;
import com.mygdx.hadal.states.ClientState;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.states.PlayState.ObjectLayer;

import java.util.UUID;

/**
 * The particle entity is an invisible, ephemeral entity that emits particle effects.
 * This is needed so that other entities can have particle effects that persist beyond their own disposal.
 * @author Crobanfoo Crubaum
 */
public class ParticleEntity extends HadalEntity {

	//What particles come out of this entity?
	private final PooledEffect effect;
	public final Particle particle;
	
	//Is this entity following another entity?
	private HadalEntity attachedEntity;
	private UUID attachedId;
	
	//How long this entity will last after deletion, the interval that this effect is turned on
	//the lifespan of this entity, how much time before dying does the effect turn off?
	private float linger, interval, lifespan, prematureTurnOff;
	
	//Has the attached entity despawned yet?
	private boolean despawn;
	
	//Will the particle despawn after a duration?
	private final boolean temp;
	
	//Is the particle currently on?
	private boolean on;
	
	//does this entity send an extra packet to sync color and scaling dynamically?
	private boolean syncExtraFields;
	
	//how is this entity synced?
	private final SyncType sync;
	
	//size multiplier of the particles
	private float scale = 1.0f;
	
	//does this effect rotate to match an attached entity?
	private boolean rotate;

	//this is the default angle of the particle velocity
	private float velocity;

	//this is the color of the particle. Nothing = base color of the effect.
	private final Vector3 color = new Vector3();
	
	//if attached to an entity, this vector is the offset of the particle from the attached entity's location
	private final Vector2 offset = new Vector2();

	//visual bounds is used to have a rough bounding box of the particle for culling offscreen effects
	private final BoundingBox visualBounds = new BoundingBox();
	private static final float visualBoundsRadius = 200.0f;

	//This constructor creates a particle effect at an area.
	public ParticleEntity(PlayState state, Vector2 startPos, Particle particle, float lifespan, boolean startOn, SyncType sync) {
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

		//as default, bounding box exists around the particle with a set size
		this.visualBounds.inf();
		this.visualBounds.ext(new Vector3(startPos.x, startPos.y, 0), visualBoundsRadius);

		setLayer(ObjectLayer.EFFECT);
	}
	
	//This constructor creates a particle effect that will follow another entity.
	public ParticleEntity(PlayState state, HadalEntity entity, Particle particle, float linger, float lifespan, boolean startOn, SyncType sync) {
		this(state, new Vector2(), particle, lifespan, startOn, sync);
		this.attachedEntity = entity;
		this.linger = linger;

		//as default, bounding box exists around the attached entity with a set size
		if (attachedEntity != null) {
			if (attachedEntity.isAlive() && attachedEntity.getBody() != null) {
				this.visualBounds.inf();
				this.visualBounds.ext(new Vector3(attachedEntity.getPixelPosition().x + offset.x, attachedEntity.getPixelPosition().y + offset.y, 0), visualBoundsRadius);
				this.effect.setPosition(attachedEntity.getPixelPosition().x + offset.x, attachedEntity.getPixelPosition().y + offset.y);
			} else {
				this.visualBounds.inf();
				this.visualBounds.ext(new Vector3(attachedEntity.getStartPos().x + offset.x, attachedEntity.getStartPos().y + offset.y, 0), visualBoundsRadius);
				this.effect.setPosition(attachedEntity.getStartPos().x + offset.x, attachedEntity.getStartPos().y + offset.y);
			}
		}
	}

	@Override
	public void create() {}

	private final Vector2 attachedLocation = new Vector2();
	private final Vector3 visualBoundsExtension = new Vector3();
	@Override
	public void controller(float delta) {
		
		//update is needed for certain properties of the effect to work properly (emitter location for culling purposes)
		effect.update(delta);
		
		//If attached to a living unit, this entity tracks its movement. If attached to a unit that has died, we despawn.
		if (attachedEntity != null && !despawn) {
			if (attachedEntity.isAlive() && attachedEntity.getBody() != null) {
				attachedLocation.set(attachedEntity.getPixelPosition());
				effect.setPosition(attachedLocation.x + offset.x, attachedLocation.y + offset.y);
				visualBoundsExtension.set(attachedLocation.x + offset.x, attachedLocation.y + offset.y, 0);
				visualBounds.ext(visualBoundsExtension, visualBoundsRadius);
			} else {
				despawn = true;
				turnOff();
			}
			
			//if rotating, set the angle equal to the angle of the attached entity
			if (rotate) {
				setParticleAngle(attachedEntity.getAngle());
			}
		}
		
		//if despawned, we delete this entity after its lingering period
		if (despawn) {
			linger -= delta;
			
			if (linger <= 0) {
				if (state.isServer()) {
					this.queueDeletion();
				} else {
					((ClientState) state).removeEntity(entityID);
				}
			}
		}

		//particles with a timer are deleted when the timer runs out. Clients remove these too if they are processing them independently from the server.
		if (temp) {
			lifespan -= delta;
			if (lifespan <= 0) {
				if (state.isServer()) {
					this.queueDeletion();
				} else {
					((ClientState) state).removeEntity(entityID);
				}
			} else if (lifespan <= prematureTurnOff) {

				//if the effect is designated to turn off before dying, do that here.
				prematureTurnOff = 0.0f;
				turnOff();
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
	 * Client ParticleEntities will run normally if set to Create or No Sync
	 * If attached to an entity that hasn't been sent over yet, wait until it exists and then attach
	 */
	@Override
	public void clientController(float delta) {
		super.clientController(delta);

		//client particles process independently from the server if they are set to CREATESYNC or NOSYNC
		if (sync.equals(SyncType.CREATESYNC) || sync.equals(SyncType.NOSYNC)) {
			controller(delta);			
		}
		
		//client particles are sometimes told to attach to a unit that the client hasn't created yet. This code makes the particle entity wait for its attached entity to be created
		if (attachedEntity == null && attachedId != null) {
			attachedEntity = state.findEntity(attachedId);
		}
	}
	
	/**
	 * Is this entity on the screen?
	 * use particle bounding box to calculate
	 */
	@Override
	public boolean isVisible() { return state.getCamera().frustum.boundsInFrustum(visualBounds); }

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
	public void render(SpriteBatch batch) {	effect.draw(batch); }

	@Override
	public void dispose() {
		if (!destroyed) {
			
			//return the effect back to the particle pool
			effect.reset();
			effect.free();
			effect.dispose();
		}
		super.dispose();
	}

	/**
	 * When created on the server, tell clients to create if create or tick sync.
	 */
	@Override
	public Object onServerCreate(boolean catchup) {
		if (sync.equals(SyncType.CREATESYNC) || sync.equals(SyncType.TICKSYNC)) {
			if (attachedEntity != null) {
				return new Packets.CreateParticles(entityID, attachedEntity.getEntityID(), offset,true, particle,
						on, linger, lifespan, prematureTurnOff, scale, rotate, velocity, sync.equals(SyncType.TICKSYNC), color);
			} else {
				return new Packets.CreateParticles(entityID, entityID, startPos, false,	particle, on, linger,
						lifespan, prematureTurnOff, scale, rotate, velocity, sync.equals(SyncType.TICKSYNC), color);
			}
		} else {
			return null;
		}
	}
	
	@Override
	public Object onServerDelete() {
		if (sync.equals(SyncType.TICKSYNC)) {
			return new Packets.DeleteEntity(entityID, state.getTimer());
		} else {
			return null;
		}
	}
	
	/**
	 * For particles that are tick synced, send over location to clients as well as whether it is on or not
	 */
	private final Vector2 newPos = new Vector2();
	@Override
	public void onServerSync() {
		if (sync.equals(SyncType.TICKSYNC)) {
			if (attachedEntity == null) {
				newPos.set(startPos);
			} else if (attachedEntity.getBody() == null) {
				newPos.set(startPos);
			} else {
				attachedLocation.set(attachedEntity.getPixelPosition());
				newPos.set(attachedLocation.x, attachedLocation.y);
			}
			//if this particle effect has extra fields (scale and color), sync those as well
			if (syncExtraFields) {
				state.getSyncPackets().add(new PacketsSync.SyncParticlesExtra(entityID, newPos, offset, entityAge,
						state.getTimer(), on, scale, color));
			} else {
				state.getSyncPackets().add(new PacketsSync.SyncParticles(entityID, newPos, offset, entityAge,
						state.getTimer(), on));
			}
		}
	}
	
	/**
	 * For Client Particle entities, sync position and on if the server sends over the packets (if Tick synced)
	 */
	@Override
	public void onClientSync(Object o) {
		if (o instanceof PacketsSync.SyncParticles p) {
			this.offset.set(p.velocity);
			effect.setPosition(p.pos.x + offset.x, p.pos.y + offset.y);

			//set bounds so that particle is not invisible to clients
			visualBoundsExtension.set(p.pos.x + offset.x, p.pos.y + offset.y, 0);
			visualBounds.ext(visualBoundsExtension, visualBoundsRadius);

			if (p.on && (!on || effect.isComplete())) {
				turnOn();
			}
			if (!p.on && (on || !effect.isComplete())) {
				turnOff();
			}
		}
		if (o instanceof PacketsSync.SyncParticlesExtra p) {
			setScale(p.scale);
			setColor(p.color);
		} else {
			super.onClientSync(o);
		}
	}
	
	/**
	 * This sets the scale of the particle
	 * we want to set the scale to the input, not just multiply the current scale by that number
	 */
	public ParticleEntity setScale(float scale) {
		if (scale != 0.0f) {
			this.effect.scaleEffect(scale / this.scale);
			this.scale = scale;
		}
		return this;
	}
	
	public void setRotate(boolean rotate) {	this.rotate = rotate; }
	
	/**
	 * Set the angle of the particle
	 */
	public ParticleEntity setParticleAngle(float angle) {
        
		float newAngle = angle * MathUtils.radDeg + 180;
		for (int i = 0; i < effect.getEmitters().size; i++) {
			ScaledNumericValue val = effect.getEmitters().get(i).getRotation();

            val.setHigh(newAngle, newAngle);
            val.setLow(newAngle);
        }
		return this;
	}

	/**
	 * Set the angle of the particle. Used for things like airblast particle movement
	 */
	public ParticleEntity setParticleVelocity(float angle) {
		this.velocity = angle;

		float newAngle = angle * MathUtils.radDeg + 180;
		for (int i = 0; i < effect.getEmitters().size; i++) {
			ScaledNumericValue val = effect.getEmitters().get(i).getAngle();

			float rotation = newAngle - val.getLowMax();
			val.setHigh(val.getHighMin() + rotation, val.getHighMax() + rotation);
			val.setLow(rotation);
		}
		return this;
	}

	/**
	 * Set the color of the particle effect
	 */
	public ParticleEntity setColor(HadalColor color) {

		//setting color to nothing means it should be unchanged, not set to white
		if (color.equals(HadalColor.NOTHING)) {
			return this;
		}

		this.color.set(color.getRGB());

		if (color.equals(HadalColor.RANDOM)) {
			
			//for random colors, each emitter is tinted with random r,b,g
			for (int i = 0; i < effect.getEmitters().size; i++) {
				float[] colors = effect.getEmitters().get(i).getTint().getColors();
				colors[0] = MathUtils.random();
				colors[1] = MathUtils.random();
				colors[2] = MathUtils.random();
			}
			return this;
		}

		for (int i = 0; i < effect.getEmitters().size; i++) {
			float[] colors = effect.getEmitters().get(i).getTint().getColors();
			colors[0] = color.getRGB().x;
			colors[1] = color.getRGB().y;
			colors[2] = color.getRGB().z;
		}
		return this;
	}

	/**
	 * This setColor is used for an rgb vector instead of a preset color.
	 */
	public ParticleEntity setColor(Vector3 color) {

		//this is a silly hack that lets clients determine random colors themselves
		if (color.x == -1.0) {
			return setColor(HadalColor.RANDOM);
		}

		this.color.set(color);
		for (int i = 0; i < effect.getEmitters().size; i++) {
			float[] colors = effect.getEmitters().get(i).getTint().getColors();
			colors[0] = color.x;
			colors[1] = color.y;
			colors[2] = color.z;
		}

		return this;
	}

	public ParticleEntity setPrematureOff(float timeLeft) {
		prematureTurnOff = timeLeft;
		return this;
	}

	public void setSyncExtraFields(boolean syncExtraFields) { this.syncExtraFields = syncExtraFields; }
	
	public PooledEffect getEffect() { return effect; }
	
	public void setAttachedEntity(HadalEntity attachedEntity) { this.attachedEntity = attachedEntity; }
	
	public void setDespawn(boolean despawn) { this.despawn = despawn; }
	
	public void setAttachedId(UUID attachedId) { this.attachedId = attachedId; }

	public void setOffset(float offsetX, float offsetY) { this.offset.set(offsetX, offsetY); }
}
