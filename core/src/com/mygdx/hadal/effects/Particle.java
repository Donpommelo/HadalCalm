package com.mygdx.hadal.effects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool.PooledEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.managers.AssetList;
import com.mygdx.hadal.schmucks.entities.ParticleEntity;

/**
 * A Particle represents a single particle effect.
 * @author Flenathan Filpbird
 */
public enum Particle {

	NOTHING(ParticleType.MISC, "", false),
	
	ARROW_BREAK(ParticleType.DEFAULT, "particles/arrowbreak.particle", false),
	BOULDER_BREAK(ParticleType.DEFAULT, "particles/boulderbreak.particle", false),
	BOW_HEAL(ParticleType.DEFAULT, "particles/bow_heal.particle", true),
	BOW_HURT(ParticleType.DEFAULT, "particles/bow_hurt.particle", true),
	BOW_TRAIL(ParticleType.DEFAULT, "particles/bow_trail.particle", true),
	BULLET_TRAIL(ParticleType.DEFAULT, "particles/bullet_trail.particle", true),
	CASINGS(ParticleType.DEFAULT, "particles/casings.particle", false),
	DEBRIS_DROP(ParticleType.DEFAULT, "particles/debrisdrop.particle", false),
	DEBRIS_TRAIL(ParticleType.DEFAULT, "particles/debristrail.particle", false),
	DIATOM_IMPACT_LARGE(ParticleType.DEFAULT, "particles/diatom_impact_large.particle", true),
	DIATOM_IMPACT_SMALL(ParticleType.DEFAULT, "particles/diatom_impact_small.particle", true),
	DIATOM_TELEGRAPH(ParticleType.DEFAULT, "particles/diatom_telegraph.particle", true),
	DIATOM_TRAIL(ParticleType.DEFAULT, "particles/diatom_trail.particle", true),
	DIATOM_TRAIL_DENSE(ParticleType.DEFAULT, "particles/diatom_trail_dense.particle", true),
	DUST(ParticleType.DEFAULT, "particles/dust.particle", false),
	ENERGY_CLOUD(ParticleType.DEFAULT, "particles/energy_cloud.particle", true),
	GLITTER(ParticleType.DEFAULT, "particles/glitter.particle", true),
	LASER(ParticleType.DEFAULT, "particles/laser.particle", true),
	LASER_PULSE(ParticleType.DEFAULT, "particles/laserpulse.particle", true),
	LASER_TRAIL(ParticleType.DEFAULT, "particles/laser_trail.particle", true),
	LASER_TRAIL_SLOW(ParticleType.DEFAULT, "particles/laser_trail_slow.particle", true),
	LASER_IMPACT(ParticleType.DEFAULT, "particles/laser_impact.particle", true),
	LIGHTNING(ParticleType.DEFAULT, "particles/lightning.particle", true),
	LIGHTNING_CHARGE(ParticleType.DEFAULT, "particles/lightning_charge.particle", true),
	LIGHTNING_BOLT(ParticleType.DEFAULT, "particles/lightning_bolt.particle", true),
	LIGHTNING_BOLT_BLUE(ParticleType.DEFAULT, "particles/lightning_bolt_blue.particle", true),
	NOTE_IMPACT(ParticleType.DEFAULT, "particles/note_impact.particle", true),
	ORB_IMPACT(ParticleType.DEFAULT, "particles/orb_impact.particle", true),
	ORB_SWIRL(ParticleType.DEFAULT, "particles/orb_swirl.particle", true),
	POLLEN_FIRE(ParticleType.DEFAULT, "particles/pollen_fire.particle", true),
	POLLEN_POISON(ParticleType.DEFAULT, "particles/pollen_poison.particle", true),
	POLYGON(ParticleType.DEFAULT, "particles/polygon.particle", true),
	REGEN(ParticleType.DEFAULT, "particles/regen.particle", true),
	RING(ParticleType.DEFAULT, "particles/ringeffect.particle", false),
	RING_TRAIL(ParticleType.DEFAULT, "particles/ring_trail.particle", true),
	SHADOW_CLOAK(ParticleType.DEFAULT, "particles/shadowcloak.particle", false),
	SHADOW_PATH(ParticleType.DEFAULT, "particles/shadowpath.particle", false),
	SHIELD(ParticleType.DEFAULT, "particles/shield.particle", true),
	SMOKE_TOTLC(ParticleType.DEFAULT, "particles/smoke.particle", true),
	SPARKLE(ParticleType.DEFAULT, "particles/sparkle.particle", true),
	SPARKS(ParticleType.DEFAULT, "particles/sparks.particle", true),
	SPLASH(ParticleType.DEFAULT, "particles/splash.particle", false),
	SPLITTER_MAIN(ParticleType.DEFAULT, "particles/splitter_main.particle", true),
	SPLITTER_TRAIL(ParticleType.DEFAULT, "particles/splitter_trail.particle", true),
	STUN(ParticleType.DEFAULT, "particles/stun.particle", false),
	TELEPORT(ParticleType.DEFAULT, "particles/teleport0.particle", true),
	TELEPORT_PRE(ParticleType.DEFAULT, "particles/preteleport.particle", true),
	WATER_BURST(ParticleType.DEFAULT, "particles/water_burst.particle", false),
	WORMHOLE(ParticleType.DEFAULT, "particles/wormhole.particle", true),

	BLIND(ParticleType.DEFAULT, "particles/blind.particle", true),
	BUBBLE_BLAST(ParticleType.DEFAULT, "particles/bubble_blast.particle", false),
	BUBBLE_TRAIL(ParticleType.DEFAULT, "particles/bubble_trail.particle", false),
	CURRENT_TRAIL(ParticleType.DEFAULT, "particles/current.particle", true),
	BUBBLE_IMPACT(ParticleType.DEFAULT, "particles/bubble_impact.particle", true),
	DIATOM(ParticleType.DEFAULT, "particles/diatom.particle", true),
	JELLY(ParticleType.DEFAULT, "particles/jelly.particle", true),
	IMPACT(ParticleType.DEFAULT, "particles/impact.particle", true),
	SPARK_TRAIL(ParticleType.DEFAULT, "particles/spark_trail.particle", true),
	POISON(ParticleType.DEFAULT, "particles/poison.particle", true),
	DANGER_BLUE(ParticleType.DEFAULT, "particles/danger_blue.particle", true),
	DANGER_RED(ParticleType.DEFAULT, "particles/danger_red.particle", true),
	EVENT_HOLO(ParticleType.DEFAULT, "particles/event_holo.particle", true),
	PICKUP_AMMO(ParticleType.DEFAULT, "particles/ammo_pickup.particle", true),
	PICKUP_ENERGY(ParticleType.DEFAULT, "particles/energy_pickup.particle", true),
	PICKUP_HEALTH(ParticleType.DEFAULT, "particles/health_pickup.particle", true),
	MOMENTUM(ParticleType.DEFAULT, "particles/momentum_freeze.particle", true),
	PORTAL(ParticleType.DEFAULT, "particles/portal.particle", true),
	SMOKE(ParticleType.DEFAULT, "particles/smoke_puff.particle", false),
	EXPLOSION(ParticleType.DEFAULT, "particles/explosion.particle", true),
	FIRE(ParticleType.DEFAULT, "particles/fire.particle", true),
	COLA_IMPACT(ParticleType.DEFAULT, "particles/cola_impact.particle", true),
	ICE_CLOUD(ParticleType.DEFAULT, "particles/ice_cloud.particle", true),
	ICE_IMPACT(ParticleType.DEFAULT, "particles/ice_frag.particle", false),
	CHARGING(ParticleType.DEFAULT, "particles/charging.particle", true),
	OVERCHARGE(ParticleType.DEFAULT, "particles/overcharge.particle", true),
	PLANT_FRAG(ParticleType.DEFAULT, "particles/plant_frag.particle", false),
	TYRRAZZA_TRAIL(ParticleType.DEFAULT, "particles/tyrrazza_trail.particle", true),
	TRICK(ParticleType.DEFAULT, "particles/trick.particle", true),
	SLODGE(ParticleType.DEFAULT, "particles/slodge.particle", false),
	SLODGE_STATUS(ParticleType.DEFAULT, "particles/slodge_status.particle", false),
	BRIGHT(ParticleType.DEFAULT, "particles/bright.particle", true),
	BRIGHT_TRAIL(ParticleType.DEFAULT, "particles/bright_trail.particle", true),
	STAR_TRAIL(ParticleType.DEFAULT, "particles/star_trail.particle", true),
	STORM(ParticleType.DEFAULT, "particles/storm.particle", false),
	PARTY(ParticleType.DEFAULT, "particles/party_ball.particle", false),
	MOREAU_LEFT(ParticleType.DEFAULT, "particles/moreau_blur_left.particle", true),
	MOREAU_RIGHT(ParticleType.DEFAULT, "particles/moreau_blur_right.particle", true),

	CONFETTI(ParticleType.DEFAULT, "particles/confetti.particle", false),
	STAR(ParticleType.DEFAULT, "particles/star_effect.particle", true),

	KAMABOKO_SHOWER(ParticleType.DEFAULT, "particles/kamaboko_shower.particle", false),
	KAMABOKO_IMPACT(ParticleType.DEFAULT, "particles/kamaboko_impact.particle", false),

	;

	//keep track of the particle pool.
	public ParticleEffectPool effectPool;
	private final ObjectMap<PooledEffect, ParticleEntity> effects = new ObjectMap<>();

	private static final int poolSize = 50;
	
	//this represents the atlas that we read the particle off of.
	private final ParticleType type;
	
	//this is the file name of the particle effect.
	private final String particleId;

	//additive is used to determine render order to minimize changing batch properties
	private final boolean additive;

	Particle(ParticleType type, String particleId, boolean additive) {
		this.type = type;
		this.particleId = particleId;
		this.additive = additive;
	}
	
	/**
	 * sets up the particle pool for this specific particle type.
	 */
	private ParticleEffect prototype;
	public void initParticlePool() {
		prototype = new ParticleEffect();
		if (type == ParticleType.DEFAULT) {
			prototype.load(Gdx.files.internal(particleId), (TextureAtlas) HadalGame.assetManager.get(AssetList.PARTICLE_ATL.toString()));
		}

		prototype.setEmittersCleanUpBlendFunction(false);
		effectPool = new ParticleEffectPool(prototype, 1, poolSize);
	}

	/**
	 * When we get a particle, we obtain it from the pool and add it to the list to keep track of
	 */
	public PooledEffect getParticle(ParticleEntity entity) {
		if (effectPool == null) {
			initParticlePool();
		}
		PooledEffect newEffect = effectPool.obtain();
		effects.put(newEffect, entity);

		return newEffect;
	}

	public PooledEffect getParticle() {
		return getParticle(null);
	}

	/**
	 * This draws all particles of this specific particle type. (if visible)
	 */
	public void drawEffects(SpriteBatch batch) {
		for (ObjectMap.Entry<PooledEffect, ParticleEntity> effect : effects.entries()) {
			if (effect.value == null) {
				effect.key.draw(batch);
			} else if (effect.value.isEffectNotCulled()) {
				effect.key.draw(batch);
			}
		}
	}

	/**
	 * This draws all particles of this specific particle type. (if visible)
	 * This is run separately to account for particles with additive blending to minimize batch setBlendFunction usage
	 */
	public static void drawParticles(SpriteBatch batch) {
		for (Particle effect : AdditiveParticles) {
			effect.drawEffects(batch);
		}
		batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		for (Particle effect : NormalParticles) {
			effect.drawEffects(batch);
		}
	}

	/**
	 * This is called when a play state is initiated
	 * It disposes of particles to free up memory
	 */
	public static void clearParticle() {
		for (Particle effect : Particle.values()) {
			for (PooledEffect e : effect.effects.keys()) {
				e.free();
			}
			effect.effects.clear();
		}
	}

	/**
	 * When we close the game, we should dispose of the particle prototypes
	 */
	public static void disposeParticlePool() {
		for (Particle effect : Particle.values()) {
			if (effect.prototype != null) {
				effect.prototype.dispose();
			}
		}
	}

	/**
	 * This frees a designated effect
	 */
	public void removeEffect(PooledEffect effect) {
		effect.free();
		effects.remove(effect);
	}

	private static final Array<Particle> NormalParticles = new Array<>();
	private static final Array<Particle> AdditiveParticles = new Array<>();
	static {
		for (Particle p : Particle.values()) {
			if (p.additive) {
				AdditiveParticles.add(p);
			} else {
				NormalParticles.add(p);
			}
		}
	}

	private enum ParticleType {
		MISC,
		DEFAULT,
	}
}
