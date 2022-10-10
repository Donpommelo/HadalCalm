package com.mygdx.hadal.strategies.hitbox;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.effects.HadalColor;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.constants.SyncType;
import com.mygdx.hadal.schmucks.entities.ParticleEntity;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.ClientState;
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

	//this is the color of the particle. change using factory method
	private HadalColor color = HadalColor.NOTHING;

	private SyncType syncType = SyncType.CREATESYNC;

	public DieParticles(PlayState state, Hitbox proj, BodyData user, Particle effect) {
		super(state, proj, user);
		this.effect = effect;
	}

	@Override
	public void die() {
		ParticleEntity particles = new ParticleEntity(state, new Vector2(hbox.getPixelPosition()), effect, duration,
			true, syncType).setColor(color);
		if (particleSize == 0) {
			particles.setScale(hbox.getScale());
		} else {
			particles.setScale(Math.min(hbox.getSize().x, MAX_SIZE) / particleSize);
		}

		if (!state.isServer()) {
			((ClientState) state).addEntity(particles.getEntityID(), particles, false, ClientState.ObjectLayer.EFFECT);
		}
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
}
