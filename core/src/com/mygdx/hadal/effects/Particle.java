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
	
	ARROW_BREAK(ParticleType.DEFAULT, "arrowbreak", false),
	BOULDER_BREAK(ParticleType.DEFAULT, "boulderbreak", false),
	BOW_HEAL(ParticleType.DEFAULT, "bow_heal", true),
	BOW_HURT(ParticleType.DEFAULT, "bow_hurt", true),
	BOW_TRAIL(ParticleType.DEFAULT, "bow_trail", true),
	BULLET_TRAIL(ParticleType.DEFAULT, "bullet_trail", true),
	CASINGS(ParticleType.DEFAULT, "casings", false),
	DEBRIS_DROP(ParticleType.DEFAULT, "debrisdrop", false),
	DEBRIS_TRAIL(ParticleType.DEFAULT, "debristrail", false),
	DIATOM_IMPACT_LARGE(ParticleType.DEFAULT, "diatom_impact_large", true),
	DIATOM_IMPACT_SMALL(ParticleType.DEFAULT, "diatom_impact_small", true),
	DIATOM_TELEGRAPH(ParticleType.DEFAULT, "diatom_telegraph", true),
	DIATOM_TRAIL(ParticleType.DEFAULT, "diatom_trail", true),
	DIATOM_TRAIL_DENSE(ParticleType.DEFAULT, "diatom_trail_dense", true),
	DUST(ParticleType.DEFAULT, "dust", false),
	ENERGY_CLOUD(ParticleType.DEFAULT, "energy_cloud", true),
	GLITTER(ParticleType.DEFAULT, "glitter", true),
	LASER(ParticleType.DEFAULT, "laser", true),
	LASER_PULSE(ParticleType.DEFAULT, "laserpulse", true),
	LASER_TRAIL(ParticleType.DEFAULT, "laser_trail", true),
	LASER_TRAIL_SLOW(ParticleType.DEFAULT, "laser_trail_slow", true),
	LASER_IMPACT(ParticleType.DEFAULT, "laser_impact", true),
	LIGHTNING(ParticleType.DEFAULT, "lightning", true),
	LIGHTNING_CHARGE(ParticleType.DEFAULT, "lightning_charge", true),
	LIGHTNING_BOLT(ParticleType.DEFAULT, "lightning_bolt", true),
	LIGHTNING_BOLT_BLUE(ParticleType.DEFAULT, "lightning_bolt_blue", true),
	NOTE_IMPACT(ParticleType.DEFAULT, "note_impact", true),
	ORB_IMPACT(ParticleType.DEFAULT, "orb_impact", true),
	ORB_SWIRL(ParticleType.DEFAULT, "orb_swirl", true),
	POLLEN_FIRE(ParticleType.DEFAULT, "pollen_fire", true),
	POLLEN_POISON(ParticleType.DEFAULT, "pollen_poison", true),
	POLYGON(ParticleType.DEFAULT, "polygon", true),
	REGEN(ParticleType.DEFAULT, "regen", true),
	RING(ParticleType.DEFAULT, "ringeffect", false),
	RING_TRAIL(ParticleType.DEFAULT, "ring_trail", true),
	SHADOW_CLOAK(ParticleType.DEFAULT, "shadowcloak", false),
	SHADOW_PATH(ParticleType.DEFAULT, "shadowpath", false),
	SHIELD(ParticleType.DEFAULT, "shield", true),
	SMOKE_TOTLC(ParticleType.DEFAULT, "smoke", true),
	SPARKLE(ParticleType.DEFAULT, "sparkle", true),
	SPARKS(ParticleType.DEFAULT, "sparks", true),
	SPLASH(ParticleType.DEFAULT, "splash", false),
	SPLITTER_MAIN(ParticleType.DEFAULT, "splitter_main", true),
	SPLITTER_TRAIL(ParticleType.DEFAULT, "splitter_trail", true),
	STUN(ParticleType.DEFAULT, "stun", false),
	TELEPORT(ParticleType.DEFAULT, "teleport0", true),
	TELEPORT_PRE(ParticleType.DEFAULT, "preteleport", true),
	WATER_BURST(ParticleType.DEFAULT, "water_burst", false),
	WORMHOLE(ParticleType.DEFAULT, "wormhole", true),

	BLIND(ParticleType.DEFAULT, "blind", true),
	BUBBLE_BLAST(ParticleType.DEFAULT, "bubble_blast", false),
	BUBBLE_TRAIL(ParticleType.DEFAULT, "bubble_trail", false),
	CURRENT_TRAIL(ParticleType.DEFAULT, "current", true),
	BUBBLE_IMPACT(ParticleType.DEFAULT, "bubble_impact", true),
	DIATOM(ParticleType.DEFAULT, "diatom", true),
	JELLY(ParticleType.DEFAULT, "jelly", true),
	IMPACT(ParticleType.DEFAULT, "impact", true),
	SPARK_TRAIL(ParticleType.DEFAULT, "spark_trail", true),
	POISON(ParticleType.DEFAULT, "poison", true),
	DANGER_BLUE(ParticleType.DEFAULT, "danger_blue", true),
	DANGER_RED(ParticleType.DEFAULT, "danger_red", true),
	EVENT_HOLO(ParticleType.DEFAULT, "event_holo", true),
	PICKUP_AMMO(ParticleType.DEFAULT, "ammo_pickup", true),
	PICKUP_ENERGY(ParticleType.DEFAULT, "energy_pickup", true),
	PICKUP_HEALTH(ParticleType.DEFAULT, "health_pickup", true),
	MOMENTUM(ParticleType.DEFAULT, "momentum_freeze", true),
	PORTAL(ParticleType.DEFAULT, "portal", true),
	SMOKE(ParticleType.DEFAULT, "smoke_puff", false),
	EXPLOSION(ParticleType.DEFAULT, "explosion", true),
	FIRE(ParticleType.DEFAULT, "fire", true),
	COLA_IMPACT(ParticleType.DEFAULT, "cola_impact", true),
	ICE_CLOUD(ParticleType.DEFAULT, "ice_cloud", true),
	ICE_IMPACT(ParticleType.DEFAULT, "ice_frag", false),
	CHARGING(ParticleType.DEFAULT, "charging", true),
	OVERCHARGE(ParticleType.DEFAULT, "overcharge", true),
	PLANT_FRAG(ParticleType.DEFAULT, "plant_frag", false),
	TYRRAZZA_TRAIL(ParticleType.DEFAULT, "tyrrazza_trail", true),
	TRICK(ParticleType.DEFAULT, "trick", true),
	SLODGE(ParticleType.DEFAULT, "slodge", false),
	SLODGE_STATUS(ParticleType.DEFAULT, "slodge_status", false),
	BRIGHT(ParticleType.DEFAULT, "bright", true),
	BRIGHT_TRAIL(ParticleType.DEFAULT, "bright_trail", true),
	STAR_TRAIL(ParticleType.DEFAULT, "star_trail", true),
	STORM(ParticleType.DEFAULT, "storm", false),
	PARTY(ParticleType.DEFAULT, "party_ball", false),
	MOREAU_LEFT(ParticleType.DEFAULT, "moreau_blur_left", true),
	MOREAU_RIGHT(ParticleType.DEFAULT, "moreau_blur_right", true),

	CONFETTI(ParticleType.DEFAULT, "confetti", false),
	STAR(ParticleType.DEFAULT, "star_effect", true),

	KAMABOKO_SHOWER(ParticleType.DEFAULT, "kamaboko_shower", false),
	KAMABOKO_IMPACT(ParticleType.DEFAULT, "kamaboko_impact", false),

	;

	private static final int POOL_SIZE = 50;

	//keep track of the particle pool.
	public ParticleEffectPool effectPool;
	private final ObjectMap<PooledEffect, ParticleEntity> effects = new ObjectMap<>();

	//this represents the atlas that we read the particle off of.
	private final ParticleType type;
	
	//this is the file name of the particle effect.
	private final String particleId;

	//additive is used to determine render order to minimize changing batch properties
	private final boolean additive;

	Particle(ParticleType type, String particleId, boolean additive) {
		this.type = type;
		this.particleId = getParticleFileName(particleId);
		this.additive = additive;
	}
	
	/**
	 * sets up the particle pool for this specific particle type.
	 */
	private ParticleEffect prototype;
	public void initParticlePool() {
		prototype = new ParticleEffect();
		if (ParticleType.DEFAULT == type)  {
			prototype.load(Gdx.files.internal(particleId), (TextureAtlas) HadalGame.assetManager.get(AssetList.PARTICLE_ATL.toString()));
		}

		prototype.setEmittersCleanUpBlendFunction(false);
		effectPool = new ParticleEffectPool(prototype, 1, POOL_SIZE);
	}

	/**
	 * When we get a particle, we obtain it from the pool and add it to the list to keep track of
	 */
	public PooledEffect getParticle(ParticleEntity entity) {
		if (null == effectPool) {
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
			if (null == effect.value) {
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
			if (null != effect.prototype) {
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

	private String getParticleFileName(String filename) {
		return "particles/" + filename + ".particle";
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
