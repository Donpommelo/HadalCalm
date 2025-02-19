package com.mygdx.hadal.strategies.hitbox;

import com.mygdx.hadal.constants.SyncType;
import com.mygdx.hadal.effects.HadalColor;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.managers.EffectEntityManager;
import com.mygdx.hadal.requests.ParticleCreate;
import com.mygdx.hadal.schmucks.entities.ParticleEntity;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.HitboxStrategy;

/**
 * This strategy creates an attached particle effect when the attached hbox is created
 * @author Scollinaire Smoregano
 */
public class CreateParticles extends HitboxStrategy {

	//this is the max hitbox size a particle will try to scale to
	private static final float maxSize = 100.0f;

	//the effect that is to be created.
	private final Particle effect;
	
	//how long should the particles last?
	private float duration;
	
	//the base size of the particle effect.
	private float particleSize;
	
	//this is the color of the particle effect
	private HadalColor color = HadalColor.NOTHING;
	
	//this is the particle effect that will be displayed
	private ParticleEntity particles;
	
	//does the particle rotate to match the velocity of the hbox (used for stuff like chain lightning)
	private boolean rotate;

	//velocity of particles. Used for things like airblast bubble movement
	private float velocity;

	//offset of particles. Used for hitboxes that are not centered on the attached entity
	private float offsetX, offsetY;

	private SyncType syncType = SyncType.NOSYNC;

	public CreateParticles(PlayState state, Hitbox proj, BodyData user, Particle effect) {
		super(state, proj, user);
		this.effect = effect;
	}
	
	@Override
	public void controller(float delta) {
		if (rotate && particles != null) {
			particles.setParticleAngle(hbox.getAngle());
		}
	}
	
	@Override
	public void create() {
		particles = EffectEntityManager.getParticle(state, new ParticleCreate(effect, hbox)
				.setLifespan(duration)
				.setSyncType(syncType));

		if (particles != null) {
			particles.setOffset(offsetX, offsetY);
			if (particleSize == 0) {
				particles.setScale(hbox.getScale());
			} else {
				particles.setScale(Math.min(hbox.getSize().y, maxSize) / particleSize);
			}

			particles.setRotate(rotate);
			particles.setColor(color);

			if (velocity != 0) {
				particles.setParticleVelocity(velocity);
			}
		}
	}

	public CreateParticles setDuration(float duration) {
		this.duration = duration;
		return this;
	}

	public CreateParticles setOffset(float offsetX, float offsetY) {
		this.offsetX = offsetX;
		this.offsetY = offsetY;
		return this;
	}

	public CreateParticles setRotate(boolean rotate) {
		this.rotate = rotate;
		return this;
	}

	public CreateParticles setParticleSize(float particleSize) {
		this.particleSize = particleSize;
		return this;
	}
	
	public CreateParticles setParticleColor(HadalColor color) {
		this.color = color;
		return this;
	}

	public CreateParticles setParticleVelocity(float velocity) {
		this.velocity = velocity;
		return this;
	}

	public CreateParticles setSyncType(SyncType syncType) {
		this.syncType = syncType;
		return this;
	}

	public ParticleEntity getParticles() { return particles; }
}
