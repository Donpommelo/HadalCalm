package com.mygdx.hadal.strategies.hitbox;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.HadalColor;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity.particleSyncType;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.HitboxStrategy;

/**
 * This strategy creates a particle effect when the attached hbox dies
 * @author Zachary Tu
 */
public class DieParticles extends HitboxStrategy {
	
	private static final float defaultDuration = 1.0f;
	
	//the effect that is to be created.
	private final Particle effect;
	
	//how long should the particles last?
	private final float duration;
	
	//the base size of the particle effect.
	private float particleSize;
	
	//this is the max hitbox size a particle will try to scale to
	private static final float maxSize = 100.0f;
		
	//this is the color of the particle. change using factory method
	private HadalColor color = HadalColor.NOTHING;

	public DieParticles(PlayState state, Hitbox proj, BodyData user, Particle effect, float duration) {
		super(state, proj, user);
		this.effect = effect;
		this.duration = duration;
	}
	
	public DieParticles(PlayState state, Hitbox proj, BodyData user, Particle effect) {
		this(state, proj, user, effect, defaultDuration);
	}
	
	@Override
	public void die() {
		ParticleEntity particle = new ParticleEntity(state, new Vector2(hbox.getPixelPosition()), effect, duration, true, particleSyncType.CREATESYNC).setColor(color);
		if (particleSize == 0) {
			particle.setScale(hbox.getScale());
		} else {
			particle.setScale(Math.min(hbox.getSize().x, maxSize) / particleSize);
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
}
