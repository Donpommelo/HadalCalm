package com.mygdx.hadal.effects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.mygdx.hadal.managers.GameStateManager;

public enum Particle {

	BUBBLE_TRAIL(ParticleType.DEFAULT, "sprites/particle/bubble_trail.particle"),
	BUBBLE_IMPACT(ParticleType.DEFAULT, "sprites/particle/bubble_impact.particle"),
	CASING(ParticleType.DEFAULT, "sprites/particle/casings.particle"),
	IMPACT(ParticleType.DEFAULT, "sprites/particle/impact.particle"),
	SPARK_TRAIL(ParticleType.DEFAULT, "sprites/particle/spark_trail.particle"),
	POISON(ParticleType.DEFAULT, "sprites/particle/poison.particle"),
	EVENT_HOLO(ParticleType.DEFAULT, "sprites/particle/event_holo.particle"),
	PICKUP_ENERGY(ParticleType.DEFAULT, "sprites/particle/energy_pickup.particle"),
	PICKUP_HEALTH(ParticleType.DEFAULT, "sprites/particle/health_pickup.particle"),
	MOMENTUM(ParticleType.DEFAULT, "sprites/particle/momentum_freeze.particle"),
	PORTAL(ParticleType.DEFAULT, "sprites/particle/portal.particle"),
	SMOKE(ParticleType.DEFAULT, "sprites/particle/smoke_puff.particle"),
	EXPLOSION(ParticleType.DEFAULT, "sprites/particle/explosion.particle"),
	FIRE(ParticleType.DEFAULT, "sprites/particle/fire.particle"),	
	
	;
	
	
	private ParticleType type;
	private String particleId;
	
	Particle(ParticleType type, String particleId) {
		this.type = type;
		this.particleId = particleId;
	}
	
	public ParticleEffect getParticle() {
		ParticleEffect newEffect = new ParticleEffect();
		switch(type) {
		case DEFAULT:
			newEffect.load(Gdx.files.internal(particleId), GameStateManager.particleAtlas);
			break;
		default:
			break;
		}
		return newEffect;
	}
	
	private enum ParticleType {
		DEFAULT
	}
}
