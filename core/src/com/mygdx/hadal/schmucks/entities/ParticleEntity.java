package com.mygdx.hadal.schmucks.entities;

import com.badlogic.gdx.graphics.g2d.ParticleEffectPool.PooledEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter.ScaledNumericValue;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.mygdx.hadal.constants.ObjectLayer;
import com.mygdx.hadal.constants.SyncType;
import com.mygdx.hadal.effects.HadalColor;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.requests.ParticleCreate;
import com.mygdx.hadal.server.packets.Packets;
import com.mygdx.hadal.states.ClientState;
import com.mygdx.hadal.states.PlayState;

import java.util.UUID;

/**
 * The particle entity is an invisible, ephemeral entity that emits particle effects.
 * This is needed so that other entities can have particle effects that persist beyond their own disposal.
 * @author Crobanfoo Crubaum
 */
public class ParticleEntity extends HadalEntity {

	private static final float VISUAL_BOUNDS_RADIUS = 200.0f;

	//What particles come out of this entity?
	private final PooledEffect effect;
	public final Particle particle;
	
	//Is this entity following another entity?
	private HadalEntity attachedEntity;
	private UUID attachedId;
	
	//the interval that this effect is turned on and the lifespan of this entity
	private float interval, lifespan;

	//Has the particle lifespan expired? (So the effect can complete before despawning)
	private boolean completing;

	//Is the particle currently on?
	private boolean on;

	//Will the particle despawn after a duration?
	private final boolean temp;
	
	//how is this entity synced?
	private final SyncType sync;
	
	//size multiplier of the particles
	private float scale = 1.0f;
	
	//does this effect rotate to match an attached entity?
	private boolean rotate;

	//does this particle render if attached to an invisible character?
	private boolean showOnInvis;

	//this is the default angle of the particle velocity
	private float velocity;

	//this is the color of the particle. Nothing = base color of the effect.
	private final Vector3 color = new Vector3();
	
	//if attached to an entity, this vector is the offset of the particle from the attached entity's location
	private final Vector2 offset = new Vector2();

	//visual bounds is used to have a rough bounding box of the particle for culling offscreen effects
	private final BoundingBox visualBounds = new BoundingBox();

	public ParticleEntity(PlayState state, ParticleCreate particleCreate) {
		super(state, particleCreate.getPosition(), new Vector2());
		this.particle = particleCreate.getParticle();
		this.effect = particle.getParticle(this);
		this.on = particleCreate.isStartOn();
		this.sync = particleCreate.getSyncType();

		this.lifespan = particleCreate.getLifespan();
		temp = lifespan != 0;

		if (particleCreate.isStartOn()) {
			this.effect.start();

			//resetting after starting prevents pooled particles from having incorrect duration timer
			this.effect.reset();
		} else {
			this.effect.allowCompletion();
		}
		this.effect.setPosition(startPos.x, startPos.y);

		//as default, bounding box exists around the particle with a set size
		this.visualBounds.inf();
		this.visualBounds.ext(new Vector3(startPos.x, startPos.y, 0), VISUAL_BOUNDS_RADIUS);

		setLayer(ObjectLayer.EFFECT);

		if (particleCreate.getAttachedEntity() != null) {
			this.attachedEntity = particleCreate.getAttachedEntity();
			//as default, bounding box exists around the attached entity with a set size
			if (attachedEntity != null) {
				if (attachedEntity.isAlive() && attachedEntity.getBody() != null) {
					this.visualBounds.inf();
					attachedLocation.set(attachedEntity.getPixelPosition());
					this.visualBounds.ext(new Vector3(attachedLocation.x + offset.x, attachedLocation.y + offset.y, 0), VISUAL_BOUNDS_RADIUS);
					this.effect.setPosition(attachedLocation.x + offset.x, attachedLocation.y + offset.y);
				} else {
					this.visualBounds.inf();
					this.visualBounds.ext(new Vector3(attachedEntity.getStartPos().x + offset.x, attachedEntity.getStartPos().y + offset.y, 0), VISUAL_BOUNDS_RADIUS);
					this.effect.setPosition(attachedEntity.getStartPos().x + offset.x, attachedEntity.getStartPos().y + offset.y);
				}
			}
		}
	}

	@Override
	public void create() {}

	private final Vector2 attachedLocation = new Vector2();
	private final Vector3 visualBoundsExtension = new Vector3();
	@Override
	public void controller(float delta) {

		//If attached to a living unit, this entity tracks its movement. If attached to a unit that has died, we despawn.
		if (attachedEntity != null && !completing) {
			if (attachedEntity.isAlive() && attachedEntity.getBody() != null) {
				attachedLocation.set(attachedEntity.getPixelPosition());
				effect.setPosition(attachedLocation.x + offset.x, attachedLocation.y + offset.y);
				visualBoundsExtension.set(attachedLocation.x + offset.x, attachedLocation.y + offset.y, 0);
				visualBounds.ext(visualBoundsExtension, VISUAL_BOUNDS_RADIUS);
			} else {
				completing = true;
				turnOff();
			}
			
			//if rotating, set the angle equal to the angle of the attached entity
			if (rotate) {
				setParticleAngle(attachedEntity.getAngle());
			}
		}

		//particles with a timer are deleted when the timer runs out. Clients remove these too if they are processing them independently from the server.
		if (temp) {
			lifespan -= delta;
			if (lifespan <= 0) {
				completing = true;
				turnOff();
			}
		}

		//particle is set to completing if lifespan is out or attached entity is deleted. isComplete() check so effect finishes fading
		if (completing && effect.isComplete()) {
			if (state.isServer()) {
				this.queueDeletion();
			} else {
				((ClientState) state).removeEntity(entityID);
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
		if (SyncType.CREATESYNC.equals(sync) || SyncType.NOSYNC.equals(sync)) {
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
	public boolean isEffectNotCulled() {
		return state.getCamera().frustum.boundsInFrustum(visualBounds);
	}

	public void turnOn() {
		if (!on || effect.isComplete()) {
			on = true;
			effect.start();
		}
	}
	
	public void turnOff() {
		if (on || !effect.isComplete()) {
			on = false;
			effect.allowCompletion();
		}
	}

	/**
	 * This turns the particle on for an input period of time
	 */
	public void onForBurst(float duration) {
		turnOn();
		interval = duration;
	}

	@Override
	public void render(SpriteBatch batch, Vector2 entityLocation) {}

	@Override
	public void dispose() {
		if (!destroyed) {

			//return the effect back to the particle pool
			particle.removeEffect(effect);
		}
		super.dispose();
	}

	/**
	 * When created on the server, tell clients to create if create or tick sync.
	 */
	@Override
	public Object onServerCreate(boolean catchup) {
		if (SyncType.CREATESYNC.equals(sync)) {
			if (attachedEntity != null) {
				return new Packets.CreateParticles(attachedEntity.getEntityID(), offset,true, particle,
						on, lifespan, scale, rotate, velocity, color);
			} else {
				return new Packets.CreateParticles(entityID, startPos, false,	particle, on, lifespan, scale, rotate, velocity, color);
			}
		} else {
			return null;
		}
	}
	
	@Override
	public Object onServerDelete() { return null; }
	
	@Override
	public void onServerSync() {}

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
	public void setParticleAngle(float angle) {
        
		float newAngle = angle * MathUtils.radDeg + 180;
		for (int i = 0; i < effect.getEmitters().size; i++) {
			ScaledNumericValue val = effect.getEmitters().get(i).getRotation();

            val.setHigh(newAngle, newAngle);
            val.setLow(newAngle);
        }
	}

	/**
	 * Set the angle of the particle. Used for things like airblast particle movement
	 */
	public void setParticleVelocity(float angle) {
		this.velocity = angle;

		float newAngle = angle * MathUtils.radDeg;
		for (int i = 0; i < effect.getEmitters().size; i++) {
			ScaledNumericValue val = effect.getEmitters().get(i).getAngle();
			float range = val.getHighMax() - val.getHighMin();

			val.setHigh(newAngle - range / 2, newAngle + range / 2);
			val.setLow(newAngle);
		}
	}

	/**
	 * Set the color of the particle effect
	 */
	public ParticleEntity setColor(HadalColor color) {

		//setting color to nothing means it should be unchanged, not set to white
		if (HadalColor.NOTHING.equals(color)) {
			return this;
		}

		this.color.set(color.getRGB());

		if (HadalColor.RANDOM.equals(color)) {
			
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

	public void setShowOnInvis(boolean showOnInvis) {
		this.showOnInvis = showOnInvis;
	}

	public PooledEffect getEffect() { return effect; }
	
	public void setAttachedEntity(HadalEntity attachedEntity) { this.attachedEntity = attachedEntity; }

	public HadalEntity getAttachedEntity() { return attachedEntity; }

	public void setAttachedId(UUID attachedId) { this.attachedId = attachedId; }

	public void setOffset(float offsetX, float offsetY) { this.offset.set(offsetX, offsetY); }

	public boolean isShowOnInvis() { return showOnInvis; }
}
