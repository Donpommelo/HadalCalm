package com.mygdx.hadal.strategies.hitbox;

import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.ParticleColors;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity.particleSyncType;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.HitboxStrategy;

/**
 * This strategy creates an attached particle effect when the attached hbox is created
 * @author Zachary Tu
 *
 */
public class CreateParticles extends HitboxStrategy {
	
	//the effect that is to be created.
	private Particle effect;
	
	//how long should the particles last? After the body is deleted?
	private float duration, linger;
	
	//the base size of the particle effect.
	//atm, this is just used for attaching particles to hitboxes of varying sizes.
	private float particleSize;
	
	private ParticleColors color = ParticleColors.NOTHING;
	
	//this is the max hitbox size a particle will try to scale to
	private static float maxSize = 100.0f;
	
	private ParticleEntity particle;
	private boolean rotate = false;
	
	public CreateParticles(PlayState state, Hitbox proj, BodyData user, Particle effect, float duration, float linger) {
		super(state, proj, user);
		this.effect = effect;
		this.duration = duration;
		this.linger = linger;
	}
	
	@Override
	public void controller(float delta) {
		if (rotate && particle != null) {
			particle.setParticleAngle(hbox.getAngle());
		}
	}
	
	@Override
	public void create() {
		particle = new ParticleEntity(state, hbox, effect, linger, duration, true, particleSyncType.CREATESYNC);
		if (particleSize == 0) {
			particle.setScale(hbox.getScale());
		} else {
			particle.setScale(Math.min(hbox.getSize().x, maxSize) / particleSize);
		}
		
		particle.setRotate(rotate);
		particle.setColor(color);
	}
	
	public CreateParticles setRotate(boolean rotate) {
		this.rotate = rotate;
		return this;
	}
	
	public CreateParticles setParticleSize(float particleSize) {
		this.particleSize = particleSize;
		return this;
	}
	
	public CreateParticles setParticleColor(ParticleColors color) {
		this.color = color;
		return this;
	}
}
