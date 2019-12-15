package com.mygdx.hadal.effects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.mygdx.hadal.managers.GameStateManager;

public enum Particle {

	BUBBLE_TRAIL(ParticleType.DEFAULT, "particles/bubble_trail.particle"),
	BUBBLE_IMPACT(ParticleType.DEFAULT, "particles/bubble_impact.particle"),
	CASINGS(ParticleType.DEFAULT, "particles/casings.particle"),
	IMPACT(ParticleType.DEFAULT, "particles/impact.particle"),
	SPARK_TRAIL(ParticleType.DEFAULT, "particles/spark_trail.particle"),
	POISON(ParticleType.DEFAULT, "particles/poison.particle"),
	EVENT_HOLO(ParticleType.DEFAULT, "particles/event_holo.particle"),
	PICKUP_ENERGY(ParticleType.DEFAULT, "particles/energy_pickup.particle"),
	PICKUP_HEALTH(ParticleType.DEFAULT, "particles/health_pickup.particle"),
	MOMENTUM(ParticleType.DEFAULT, "particles/momentum_freeze.particle"),
	PORTAL(ParticleType.DEFAULT, "particles/portal.particle"),
	SMOKE(ParticleType.DEFAULT, "particles/smoke_puff.particle"),
	EXPLOSION(ParticleType.DEFAULT, "particles/explosion.particle"),
	FIRE(ParticleType.DEFAULT, "particles/fire.particle"),	
	
	DUST(ParticleType.TOTLC, "particles/dust.particle"),	
	RING(ParticleType.TOTLC, "particles/ringeffect.particle"),	
	SPARKLE(ParticleType.TOTLC, "particles/sparkle.particle"),	
	SPARKS(ParticleType.TOTLC, "particles/sparks.particle"),	
	SPLASH(ParticleType.TOTLC, "particles/splash.particle"),	
	STUN(ParticleType.TOTLC, "particles/stun.particle"),	
	TELEPORT(ParticleType.TOTLC, "particles/teleport0.particle"),	
	WATER_BURST(ParticleType.TOTLC, "particles/water_burst.particle"),	
	WORMHOLE(ParticleType.TOTLC, "particles/wormhole.particle"),	
	LASER(ParticleType.TOTLC, "particles/laser.particle"),	
	LASER_PULSE(ParticleType.TOTLC, "particles/laserpulse.particle"),	
	LIGHTNING(ParticleType.TOTLC, "particles/lightning.particle"),	
	REGEN(ParticleType.TOTLC, "particles/regen.particle"),	
	SHADOW_CLOAK(ParticleType.TOTLC, "particles/shadowcloak.particle"),	
	SHADOW_PATH(ParticleType.TOTLC, "particles/shadowpath.particle"),	
	SHIELD(ParticleType.TOTLC, "particles/shield.particle"),	

	CONFETTI(ParticleType.PARTY, "particles/confetti.particle"),	

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
		case TOTLC:
			newEffect.load(Gdx.files.internal(particleId), GameStateManager.particleTOTLCAtlas);
			break;
		case PARTY:
			newEffect.load(Gdx.files.internal(particleId), GameStateManager.partycleAtlas);
			break;
		default:
			break;
		}
		return newEffect;
	}
	
	private enum ParticleType {
		DEFAULT,
		TOTLC,
		PARTY
	}
}
