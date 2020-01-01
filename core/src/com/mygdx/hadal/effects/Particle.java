package com.mygdx.hadal.effects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.mygdx.hadal.managers.GameStateManager;

public enum Particle {

	NOTHING(ParticleType.MISC, ""),
	
	ARROW_BREAK(ParticleType.DEFAULT, "particles/totlc/arrowbreak.particle"),
	BOULDER_BREAK(ParticleType.DEFAULT, "particles/totlc/boulderbreak.particle"),
	CASINGS(ParticleType.DEFAULT, "particles/totlc/casings.particle"),
	DEBRIS_DROP(ParticleType.DEFAULT, "particles/totlc/debrisdrop.particle"),	
	DEBRIS_TRAIL(ParticleType.DEFAULT, "particles/totlc/debristrail.particle"),	
	DUST(ParticleType.DEFAULT, "particles/totlc/dust.particle"),	
	LASER(ParticleType.DEFAULT, "particles/totlc/laser.particle"),	
	LASER_PULSE(ParticleType.DEFAULT, "particles/totlc/laserpulse.particle"),	
	LIGHTNING(ParticleType.DEFAULT, "particles/totlc/lightning.particle"),	
	REGEN(ParticleType.DEFAULT, "particles/totlc/regen.particle"),	
	RING(ParticleType.DEFAULT, "particles/totlc/ringeffect.particle"),
	SHADOW_CLOAK(ParticleType.DEFAULT, "particles/totlc/shadowcloak.particle"),	
	SHADOW_PATH(ParticleType.DEFAULT, "particles/totlc/shadowpath.particle"),	
	SHIELD(ParticleType.DEFAULT, "particles/totlc/shield.particle"),
	SMOKE_TOTLC(ParticleType.DEFAULT, "particles/totlc/smoke.particle"),
	SPARKLE(ParticleType.DEFAULT, "particles/totlc/sparkle.particle"),	
	SPARKS(ParticleType.DEFAULT, "particles/totlc/sparks.particle"),	
	SPLASH(ParticleType.DEFAULT, "particles/totlc/splash.particle"),	
	STUN(ParticleType.DEFAULT, "particles/totlc/stun.particle"),	
	TELEPORT(ParticleType.DEFAULT, "particles/totlc/teleport0.particle"),	
	WATER_BURST(ParticleType.DEFAULT, "particles/totlc/water_burst.particle"),	
	WORMHOLE(ParticleType.DEFAULT, "particles/totlc/wormhole.particle"),
	
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
	
	CONFETTI(ParticleType.DEFAULT, "particles/totlc/confetti.particle"),	
	STAR(ParticleType.DEFAULT, "particles/totlc/star_effect.particle"),	

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
		MISC,
		DEFAULT,
	}
}
