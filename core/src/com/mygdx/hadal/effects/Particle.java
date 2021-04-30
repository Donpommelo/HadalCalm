package com.mygdx.hadal.effects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool.PooledEffect;
import com.mygdx.hadal.managers.GameStateManager;

/**
 * A Particle represents a single particle effect.
 * @author Flenathan Filpbird
 */
public enum Particle {

	NOTHING(ParticleType.MISC, ""),
	
	ARROW_BREAK(ParticleType.DEFAULT, "particles/arrowbreak.particle"),
	BOULDER_BREAK(ParticleType.DEFAULT, "particles/boulderbreak.particle"),
	CASINGS(ParticleType.DEFAULT, "particles/casings.particle"),
	DEBRIS_DROP(ParticleType.DEFAULT, "particles/debrisdrop.particle"),
	DEBRIS_TRAIL(ParticleType.DEFAULT, "particles/debristrail.particle"),
	DIATOM_IMPACT_LARGE(ParticleType.DEFAULT, "particles/diatom_impact_large.particle"),
	DIATOM_IMPACT_SMALL(ParticleType.DEFAULT, "particles/diatom_impact_small.particle"),
	DIATOM_TELEGRAPH(ParticleType.DEFAULT, "particles/diatom_telegraph.particle"),
	DIATOM_TRAIL(ParticleType.DEFAULT, "particles/diatom_trail.particle"),
	DIATOM_TRAIL_DENSE(ParticleType.DEFAULT, "particles/diatom_trail_dense.particle"),
	DUST(ParticleType.DEFAULT, "particles/dust.particle"),
	LASER(ParticleType.DEFAULT, "particles/laser.particle"),	
	LASER_PULSE(ParticleType.DEFAULT, "particles/laserpulse.particle"),
	LASER_TRAIL(ParticleType.DEFAULT, "particles/laser_trail.particle"),
	LASER_TRAIL_SLOW(ParticleType.DEFAULT, "particles/laser_trail_slow.particle"),
	LASER_IMPACT(ParticleType.DEFAULT, "particles/laser_impact.particle"),
	LIGHTNING(ParticleType.DEFAULT, "particles/lightning.particle"),	
	LIGHTNING_CHARGE(ParticleType.DEFAULT, "particles/lightning_charge.particle"),	
	LIGHTNING_BOLT(ParticleType.DEFAULT, "particles/lightning_bolt.particle"),
	LIGHTNING_BOLT_BLUE(ParticleType.DEFAULT, "particles/lightning_bolt_blue.particle"),
	POLLEN_FIRE(ParticleType.DEFAULT, "particles/pollen_fire.particle"),
	POLLEN_POISON(ParticleType.DEFAULT, "particles/pollen_poison.particle"),
	POLYGON(ParticleType.DEFAULT, "particles/polygon.particle"),
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

	BUBBLE_BLAST(ParticleType.DEFAULT, "particles/bubble_blast.particle"),
	BUBBLE_TRAIL(ParticleType.DEFAULT, "particles/bubble_trail.particle"),
	CURRENT_TRAIL(ParticleType.DEFAULT, "particles/current_horizontal.particle"),
	BUBBLE_IMPACT(ParticleType.DEFAULT, "particles/bubble_impact.particle"),
	DIATOM(ParticleType.DEFAULT, "particles/diatom.particle"),
	JELLY(ParticleType.DEFAULT, "particles/jelly.particle"),
	IMPACT(ParticleType.DEFAULT, "particles/impact.particle"),
	SPARK_TRAIL(ParticleType.DEFAULT, "particles/spark_trail.particle"),
	POISON(ParticleType.DEFAULT, "particles/poison.particle"),
	DANGER_BLUE(ParticleType.DEFAULT, "particles/danger_blue.particle"),
	DANGER_RED(ParticleType.DEFAULT, "particles/danger_red.particle"),
	EVENT_HOLO(ParticleType.DEFAULT, "particles/event_holo.particle"),
	PICKUP_ENERGY(ParticleType.DEFAULT, "particles/energy_pickup.particle"),
	PICKUP_HEALTH(ParticleType.DEFAULT, "particles/health_pickup.particle"),
	MOMENTUM(ParticleType.DEFAULT, "particles/momentum_freeze.particle"),
	PORTAL(ParticleType.DEFAULT, "particles/portal.particle"),
	SMOKE(ParticleType.DEFAULT, "particles/smoke_puff.particle"),
	EXPLOSION(ParticleType.DEFAULT, "particles/explosion.particle"),
	FIRE(ParticleType.DEFAULT, "particles/fire.particle"),	
	COLA_IMPACT(ParticleType.DEFAULT, "particles/cola_impact.particle"),	
	ICE_CLOUD(ParticleType.DEFAULT, "particles/ice_cloud.particle"),	
	ICE_IMPACT(ParticleType.DEFAULT, "particles/ice_frag.particle"),	
	CHARGING(ParticleType.DEFAULT, "particles/charging.particle"),	
	OVERCHARGE(ParticleType.DEFAULT, "particles/overcharge.particle"),
	PLANT_FRAG(ParticleType.DEFAULT, "particles/plant_frag.particle"),
	TYRRAZZA_TRAIL(ParticleType.DEFAULT, "particles/tyrrazza_trail.particle"),
	TRICK(ParticleType.DEFAULT, "particles/trick.particle"),
	SLODGE(ParticleType.DEFAULT, "particles/slodge.particle"),
	SLODGE_STATUS(ParticleType.DEFAULT, "particles/slodge_status.particle"),	
	BRIGHT(ParticleType.DEFAULT, "particles/bright.particle"),	
	STORM(ParticleType.DEFAULT, "particles/storm.particle"),
	PARTY(ParticleType.DEFAULT, "particles/party_ball.particle"),
	MOREAU_LEFT(ParticleType.DEFAULT, "particles/moreau_blur_left.particle"),
	MOREAU_RIGHT(ParticleType.DEFAULT, "particles/moreau_blur_right.particle"),


	CONFETTI(ParticleType.DEFAULT, "particles/confetti.particle"),	
	STAR(ParticleType.DEFAULT, "particles/star_effect.particle"),	

	KAMABOKO_SHOWER(ParticleType.DEFAULT, "particles/kamaboko_shower.particle"),	
	KAMABOKO_IMPACT(ParticleType.DEFAULT, "particles/kamaboko_impact.particle"),	
	;
	
	//keep track of the particle pool.
	public static ParticleEffectPool effectPool;
	private static final int poolSize = 75;
	
	//this represents the atlas that we read the particle off of.
	private final ParticleType type;
	
	//this is the file name of the particle effect.
	private final String particleId;
	
	Particle(ParticleType type, String particleId) {
		this.type = type;
		this.particleId = particleId;
	}
	
	/**
	 * sets up the particle pool.
	 */
	private static ParticleEffect prototype;
	public static void initParticlePool() {
		prototype = new ParticleEffect();
		effectPool = new ParticleEffectPool(prototype, 0, poolSize);
	}

	/**
	 * When we close the game, we should dispose of the particle prototype
	 */
	public static void disposeParticlePool() {
		if (prototype != null) {
			prototype.dispose();
		}
	}

	/**
	 * When we get a particle, we obtain it from the pool, reset it and load it.
	 * resetting is necessary to prevent the very first particle from not showing up properly
	 */
	public PooledEffect getParticle() {
		PooledEffect newEffect = effectPool.obtain();
		newEffect.reset();
		if (type == ParticleType.DEFAULT) {
			newEffect.load(Gdx.files.internal(particleId), GameStateManager.particleAtlas);
		}
		return newEffect;
	}
	
	private enum ParticleType {
		MISC,
		DEFAULT,
	}
}
