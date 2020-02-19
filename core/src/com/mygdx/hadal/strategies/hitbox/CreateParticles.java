package com.mygdx.hadal.strategies.hitbox;

import com.mygdx.hadal.effects.Particle;
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
	
	private final static float defaultDuration = 0.0f;
	private final static float defaultLinger = 3.0f;
	
	//the effect that is to be created.
	private Particle effect;
	
	//how long should the particles last?
	private float duration;
	
	public CreateParticles(PlayState state, Hitbox proj, BodyData user, Particle effect, float duration) {
		super(state, proj, user);
		this.effect = effect;
		this.duration = duration;
	}
	
	public CreateParticles(PlayState state, Hitbox proj, BodyData user, Particle effect) {
		this(state, proj, user, effect, defaultDuration);
	}
	
	@Override
	public void create() {
		ParticleEntity particle = new ParticleEntity(state, hbox, effect, defaultLinger, duration, true, particleSyncType.CREATESYNC);
		particle.setScale(hbox.getScale());
	}
}
