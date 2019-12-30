package com.mygdx.hadal.effects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.mygdx.hadal.managers.GameStateManager;

public enum Particle {

	NOTHING(ParticleType.MISC, ""),
	
	ARROW_BREAK(ParticleType.TOTLC, "particles/totlc/arrowbreak.particle"),
	BOULDER_BREAK(ParticleType.TOTLC, "particles/totlc/boulderbreak.particle"),
	CASINGS(ParticleType.TOTLC, "particles/totlc/casings.particle"),
	DEBRIS_DROP(ParticleType.TOTLC, "particles/totlc/debrisdrop.particle"),	
	DEBRIS_TRAIL(ParticleType.TOTLC, "particles/totlc/debristrail.particle"),	
	DUST(ParticleType.TOTLC, "particles/totlc/dust.particle"),	
	LASER(ParticleType.TOTLC, "particles/totlc/laser.particle"),	
	LASER_PULSE(ParticleType.TOTLC, "particles/totlc/laserpulse.particle"),	
	LIGHTNING(ParticleType.TOTLC, "particles/totlc/lightning.particle"),	
	REGEN(ParticleType.TOTLC, "particles/totlc/regen.particle"),	
	RING(ParticleType.TOTLC, "particles/totlc/ringeffect.particle"),
	SHADOW_CLOAK(ParticleType.TOTLC, "particles/totlc/shadowcloak.particle"),	
	SHADOW_PATH(ParticleType.TOTLC, "particles/totlc/shadowpath.particle"),	
	SHIELD(ParticleType.TOTLC, "particles/totlc/shield.particle"),
	SMOKE_TOTLC(ParticleType.TOTLC, "particles/totlc/smoke.particle"),
	SPARKLE(ParticleType.TOTLC, "particles/totlc/sparkle.particle"),	
	SPARKS(ParticleType.TOTLC, "particles/totlc/sparks.particle"),	
	SPLASH(ParticleType.TOTLC, "particles/totlc/splash.particle"),	
	STUN(ParticleType.TOTLC, "particles/totlc/stun.particle"),	
	TELEPORT(ParticleType.TOTLC, "particles/totlc/teleport0.particle"),	
	WATER_BURST(ParticleType.TOTLC, "particles/totlc/water_burst.particle"),	
	WORMHOLE(ParticleType.TOTLC, "particles/totlc/wormhole.particle"),
	
	BUBBLE_TRAIL(ParticleType.DEFAULT, "particles/bubble_trail.particle"),
	BUBBLE_IMPACT(ParticleType.DEFAULT, "particles/bubble_impact.particle"),
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
	
	CONFETTI(ParticleType.PARTY, "particles/totlc/confetti.particle"),	
	STAR(ParticleType.STAR, "particles/totlc/star_effect.particle"),	

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
		case STAR:
			newEffect.load(Gdx.files.internal(particleId), GameStateManager.starAtlas);
			break;
		default:
			break;
		}
		return newEffect;
	}
	
	private enum ParticleType {
		MISC,
		DEFAULT,
		TOTLC,
		PARTY,
		STAR
	}
}
