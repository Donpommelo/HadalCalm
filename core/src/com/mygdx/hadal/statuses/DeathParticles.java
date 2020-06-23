package com.mygdx.hadal.statuses;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity.particleSyncType;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;

public class DeathParticles extends Status {
	
	private Particle particle;
	private float duration;
	
	public DeathParticles(PlayState state, BodyData p, Particle particle, float duration) {
		super(state, p);
		this.particle = particle;
		this.duration = duration;
	}
	
	@Override
	public void onDeath(BodyData perp) {
		new ParticleEntity(state, new Vector2(inflicted.getSchmuck().getPixelPosition()), particle, duration, true, particleSyncType.CREATESYNC);
	}
}
