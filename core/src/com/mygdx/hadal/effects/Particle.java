package com.mygdx.hadal.effects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool.PooledEffect;
import com.mygdx.hadal.managers.GameStateManager;

public enum Particle {

	NOTHING(ParticleType.MISC, ""),
	
	ARROW_BREAK(ParticleType.DEFAULT, "particles/arrowbreak.particle"),
	BOULDER_BREAK(ParticleType.DEFAULT, "particles/boulderbreak.particle"),
	CASINGS(ParticleType.DEFAULT, "particles/casings.particle"),
	DEBRIS_DROP(ParticleType.DEFAULT, "particles/debrisdrop.particle"),	
	DEBRIS_TRAIL(ParticleType.DEFAULT, "particles/debristrail.particle"),	
	DUST(ParticleType.DEFAULT, "particles/dust.particle"),	
	LASER(ParticleType.DEFAULT, "particles/laser.particle"),	
	LASER_PULSE(ParticleType.DEFAULT, "particles/laserpulse.particle"),	
	LIGHTNING(ParticleType.DEFAULT, "particles/lightning.particle"),	
	REGEN(ParticleType.DEFAULT, "particles/regen.particle"),	
	RING(ParticleType.DEFAULT, "particles/ringeffect.particle"),
	SHADOW_CLOAK(ParticleType.DEFAULT, "particles/shadowcloak.particle"),	
	SHADOW_PATH(ParticleType.DEFAULT, "particles/shadowpath.particle"),	
	SHIELD(ParticleType.DEFAULT, "particles/shield.particle"),
	SMOKE_TOTLC(ParticleType.DEFAULT, "particles/smoke.particle"),
	SPARKLE(ParticleType.DEFAULT, "particles/sparkle.particle"),	
	SPARKS(ParticleType.DEFAULT, "particles/sparks.particle"),	
	SPLASH(ParticleType.DEFAULT, "particles/splash.particle"),	
	STUN(ParticleType.DEFAULT, "particles/stun.particle"),	
	TELEPORT(ParticleType.DEFAULT, "particles/teleport0.particle"),	
	WATER_BURST(ParticleType.DEFAULT, "particles/water_burst.particle"),	
	WORMHOLE(ParticleType.DEFAULT, "particles/wormhole.particle"),
	
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
	
	CONFETTI(ParticleType.DEFAULT, "particles/confetti.particle"),	
	STAR(ParticleType.DEFAULT, "particles/star_effect.particle"),	

	;
	
	private static ParticleEffect prototype;
	private static ParticleEffectPool effectPool;
	private final static int poolSize = 50;
	
	private ParticleType type;
	private String particleId;
	
	Particle(ParticleType type, String particleId) {
		this.type = type;
		this.particleId = particleId;
	}
	
	public static void initParticlePool() {
		prototype = new ParticleEffect();
		effectPool = new ParticleEffectPool(prototype, 0, poolSize);
	}
	
	public PooledEffect getParticle() {
		PooledEffect newEffect = effectPool.obtain();
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
