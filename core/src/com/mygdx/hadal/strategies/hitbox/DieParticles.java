package com.mygdx.hadal.strategies.hitbox;

import com.mygdx.hadal.constants.SyncType;
import com.mygdx.hadal.effects.HadalColor;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.managers.EffectEntityManager;
import com.mygdx.hadal.requests.ParticleCreate;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.HitboxStrategy;

/**
 * This strategy creates a particle effect when the attached hbox dies
 * @author Svestarossa Scolomon
 */
public class DieParticles extends HitboxStrategy {
	
	private static final float DEFAULT_DURATION = 1.0f;

	//this is the max hitbox size a particle will try to scale to
	private static final float MAX_SIZE = 100.0f;

	//the effect that is to be created.
	private final Particle effect;
	
	//how long should the particles last?
	private float duration = DEFAULT_DURATION;
	
	//the base size of the particle effect.
	private float particleSize;

	//if true, this will not play if the hbox dies by timing out
	private boolean ignoreOnTimeout;

	//this is the color of the particle. change using factory method
	private HadalColor color = HadalColor.NOTHING;

	private SyncType syncType = SyncType.NOSYNC;

	public DieParticles(PlayState state, Hitbox proj, BodyData user, Particle effect) {
		super(state, proj, user);
		this.effect = effect;
	}

	@Override
	public void die() {

		if (ignoreOnTimeout && hbox.getLifeSpan() <= 0.0f) { return; }

		ParticleCreate particleCreate = new ParticleCreate(effect, hbox.getPixelPosition())
				.setLifespan(duration)
				.setSyncType(syncType)
				.setColor(color);

		if (particleSize == 0) {
			particleCreate.setScale(hbox.getScale());
		} else {
			particleCreate.setScale(Math.min(hbox.getSize().x, MAX_SIZE) / particleSize);
		}

		EffectEntityManager.getParticle(state, particleCreate);
	}
	
	public DieParticles setParticleSize(float particleSize) {
		this.particleSize = particleSize;
		return this;
	}
	
	public DieParticles setParticleColor(HadalColor color) {
		this.color = color;
		return this;
	}

	public DieParticles setParticleDuration(float duration) {
		this.duration = duration;
		return this;
	}

	public DieParticles setSyncType(SyncType syncType) {
		this.syncType = syncType;
		return this;
	}

	public DieParticles setIgnoreOnTimeout(boolean ignoreOnTimeout) {
		this.ignoreOnTimeout = ignoreOnTimeout;
		return this;
	}
}
