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
import com.mygdx.hadal.schmucks.entities.Player;

/**
 * A Particle represents a single particle effect.
 * @author Flenathan Filpbird
 */
public enum Particle {

	NOTHING(ParticleType.MISC, "", false),

	//General
	BLIND(ParticleType.DEFAULT, "blind", true),
	BUBBLE_BLAST(ParticleType.DEFAULT, "bubble_blast", false),
	BUBBLE_TRAIL(ParticleType.DEFAULT, "bubble_trail", false),
	BUBBLE_IMPACT(ParticleType.DEFAULT, "bubble_impact", true),
	BRIGHT(ParticleType.DEFAULT, "bright", true),
	CHARGING(ParticleType.DEFAULT, "charging", true),
	DANGER_BLUE(ParticleType.DEFAULT, "danger_blue", true),
	DANGER_RED(ParticleType.DEFAULT, "danger_red", true),
	DUST(ParticleType.DEFAULT, "dust", false),
	EXPLOSION(ParticleType.DEFAULT, "explosion", true),
	FIRE(ParticleType.DEFAULT, "fire", true),
	GLITTER(ParticleType.DEFAULT, "glitter", true),
	IMPACT(ParticleType.DEFAULT, "impact", true),
	KAMABOKO_SHOWER(ParticleType.DEFAULT, "kamaboko_shower", false),
	KAMABOKO_IMPACT(ParticleType.DEFAULT, "kamaboko_impact", false),
	LASER_PULSE(ParticleType.DEFAULT, "laserpulse", true),
	LASER_TRAIL(ParticleType.DEFAULT, "laser_trail", true),
	LASER_TRAIL_SLOW(ParticleType.DEFAULT, "laser_trail_slow", true),
	LASER_IMPACT(ParticleType.DEFAULT, "laser_impact", true),
	LIGHTNING(ParticleType.DEFAULT, "lightning", true),
	LIGHTNING_CHARGE(ParticleType.DEFAULT, "lightning_charge", true),
	LIGHTNING_BOLT(ParticleType.DEFAULT, "lightning_bolt", true),
	MOMENTUM(ParticleType.DEFAULT, "momentum_freeze", true),
	MOREAU_LEFT(ParticleType.DEFAULT, "moreau_blur_left", true),
	MOREAU_RIGHT(ParticleType.DEFAULT, "moreau_blur_right", true),
	OVERCHARGE(ParticleType.DEFAULT, "overcharge", true),
	REGEN(ParticleType.DEFAULT, "regen", true),
	RING(ParticleType.DEFAULT, "ringeffect", false),
	SHADOW_PATH(ParticleType.DEFAULT, "shadowpath", false),
	SHIELD(ParticleType.DEFAULT, "shield", true),
	SMOKE(ParticleType.DEFAULT, "smoke_puff", false),
	SMOKE_TOTLC(ParticleType.DEFAULT, "smoke", true),
	SPARKLE(ParticleType.DEFAULT, "sparkle", true),
	SPARK_TRAIL(ParticleType.DEFAULT, "spark_trail", true),
	SPARKS(ParticleType.DEFAULT, "sparks", true),
	SPLASH(ParticleType.DEFAULT, "splash", false),
	STUN(ParticleType.DEFAULT, "stun", false),
	TELEPORT(ParticleType.DEFAULT, "teleport0", true),
	TELEPORT_PRE(ParticleType.DEFAULT, "preteleport", true),
	WATER_BURST(ParticleType.DEFAULT, "water_burst", false),
	WORMHOLE(ParticleType.DEFAULT, "wormhole", true),

	//Weapon
	ARROW_BREAK(ParticleType.DEFAULT, "arrowbreak", false),
	BEACH_BALL_TRAIL(ParticleType.DEFAULT, "beach_ball_trail", true),
	BOW_HEAL(ParticleType.DEFAULT, "bow_heal", true),
	BOW_HURT(ParticleType.DEFAULT, "bow_hurt", true),
	BOW_TRAIL(ParticleType.DEFAULT, "bow_trail", true),
	BULLET_TRAIL(ParticleType.DEFAULT, "bullet_trail", true),
	COLA_IMPACT(ParticleType.DEFAULT, "cola_impact", true),
	DIATOM_IMPACT_SMALL(ParticleType.DEFAULT, "diatom_impact_small", true),
	DIATOM_TRAIL(ParticleType.DEFAULT, "diatom_trail", true),
	DIATOM_TRAIL_DENSE(ParticleType.DEFAULT, "diatom_trail_dense", true),
	ICE_CLOUD(ParticleType.DEFAULT, "ice_cloud", true),
	ICE_IMPACT(ParticleType.DEFAULT, "ice_frag", false),
	LIFE_STEAL(ParticleType.DEFAULT, "lifesteal", true),
	NAIL_BURST(ParticleType.DEFAULT, "urchin_burst", true),
	NAIL_IMPACT(ParticleType.DEFAULT, "urchin_impact", true),
	NAIL_TRAIL(ParticleType.DEFAULT, "urchin_trail", true),
	NOTE_IMPACT(ParticleType.DEFAULT, "note_impact", true),
	NEBULA(ParticleType.DEFAULT, "death_orb", true),
	NEBULA_DESPAWN(ParticleType.DEFAULT, "death_orb_despawn", true),
	ORB_IMPACT(ParticleType.DEFAULT, "orb_impact", true),
	ORB_SWIRL(ParticleType.DEFAULT, "orb_swirl", true),
	PARTY(ParticleType.DEFAULT, "party_ball", false),
	POLYGON(ParticleType.DEFAULT, "polygon", true),
	PLANT_FRAG(ParticleType.DEFAULT, "plant_frag", false),
	RING_TRAIL(ParticleType.DEFAULT, "ring_trail", true),
	SLODGE(ParticleType.DEFAULT, "slodge", false),
	SLODGE_STATUS(ParticleType.DEFAULT, "slodge_status", false),
	SPLITTER_MAIN(ParticleType.DEFAULT, "splitter_main", true),
	SPLITTER_TRAIL(ParticleType.DEFAULT, "splitter_trail", true),
	STARBURST(ParticleType.DEFAULT, "starburst", true),
	STORM(ParticleType.DEFAULT, "storm", false),
	TRICK(ParticleType.DEFAULT, "trick", true),
	TYRRAZZA_TRAIL(ParticleType.DEFAULT, "tyrrazza_trail", true),
	TRIDENT_TRAIL(ParticleType.DEFAULT, "trident_trail", true),
	WISP_TRAIL(ParticleType.DEFAULT, "wisp_trail_left", false),
	VAMPIRE(ParticleType.DEFAULT, "vampire", true),


	//Magic
	GHOST_LIGHT(ParticleType.DEFAULT, "ghostlight", true, true),
	KRILL_ALERT(ParticleType.DEFAULT, "krill_alert", true),
	KRILL_LEFT(ParticleType.DEFAULT, "krill_command_l", true),
	KRILL_RIGHT(ParticleType.DEFAULT, "krill_command_r", true),
	STAR_TRAIL(ParticleType.DEFAULT, "star_trail", true),

	//Event
	BOULDER_BREAK(ParticleType.DEFAULT, "boulderbreak", false),
	BRIGHT_TRAIL(ParticleType.DEFAULT, "bright_trail", true),
	CURRENT_TRAIL(ParticleType.DEFAULT, "current", true),
	DIATOM_IMPACT_LARGE(ParticleType.DEFAULT, "diatom_impact_large", true),
	EVENT_HOLO(ParticleType.DEFAULT, "event_holo", true),
	PICKUP_AMMO(ParticleType.DEFAULT, "ammo_pickup", true),
	PICKUP_ENERGY(ParticleType.DEFAULT, "energy_pickup", true),
	PICKUP_HEALTH(ParticleType.DEFAULT, "health_pickup", true),
	POISON(ParticleType.DEFAULT, "poison", true),
	PORTAL(ParticleType.DEFAULT, "portal", true),

	//Enemy
	DIATOM_TELEGRAPH(ParticleType.DEFAULT, "diatom_telegraph", true),
	LASER(ParticleType.DEFAULT, "laser", true),
	POLLEN_FIRE(ParticleType.DEFAULT, "pollen_fire", true),
	POLLEN_POISON(ParticleType.DEFAULT, "pollen_poison", true),
	STAR(ParticleType.DEFAULT, "star_effect", true),

	//UI
	DIATOM(ParticleType.DEFAULT, "diatom", true),
	JELLY(ParticleType.DEFAULT, "jelly", true),

	//Tentatively Unused
	CASINGS(ParticleType.DEFAULT, "casings", false),
	CONFETTI(ParticleType.DEFAULT, "confetti", false),
	DEBRIS_DROP(ParticleType.DEFAULT, "debrisdrop", false),
	DEBRIS_TRAIL(ParticleType.DEFAULT, "debristrail", false),
	ENERGY_CLOUD(ParticleType.DEFAULT, "energy_cloud", true),
	GRADIENT_TRAIL(ParticleType.DEFAULT, "gradient_trail", true),
	LIGHTNING_BOLT_BLUE(ParticleType.DEFAULT, "lightning_bolt_blue", true),
	SHADOW_CLOAK(ParticleType.DEFAULT, "shadowcloak", false),
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

	//if true, this effect will be rendered under entities instead of above
	private final boolean bottom;

	//this keeps track of the particle's original colors, so changed colors are reset when pulled from the pool
	private final Array<float[]> originalColors = new Array<>();

	Particle(ParticleType type, String particleId, boolean additive, boolean bottom) {
		this.type = type;
		this.particleId = getParticleFileName(particleId);
		this.additive = additive;
		this.bottom = bottom;
	}

	Particle(ParticleType type, String particleId, boolean additive) {
		this(type, particleId, additive, false);
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

		//save original colors so we can reset later in case of color changes
		for (int i = 0; i < prototype.getEmitters().size; i++) {
			originalColors.add(prototype.getEmitters().get(i).getTint().getColors());
		}
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

		//reset colors to original
		if (originalColors.size == newEffect.getEmitters().size) {
			for (int i = 0; i < newEffect.getEmitters().size; i++) {
				if (originalColors.get(i).length == 3) {
					float[] colors = newEffect.getEmitters().get(i).getTint().getColors();
					colors[0] = originalColors.get(i)[0];
					colors[1] = originalColors.get(i)[1];
					colors[2] = originalColors.get(i)[2];
				}
			}
		}

		return newEffect;
	}

	public PooledEffect getParticle() {
		return getParticle(null);
	}

	/**
	 * This draws all particles of this specific particle type. (if visible)
	 */
	public void drawEffects(SpriteBatch batch, float delta) {
		for (ObjectMap.Entry<PooledEffect, ParticleEntity> effect : effects.entries()) {

			//null values indicate a non-entity particle (used in menus like results screen confetti)
			if (effect.value == null) {
				effect.key.draw(batch, delta);
			} else if (effect.value.isEffectNotCulled()) {
				if (!effect.value.isShowOnInvis()
						&& null != effect.value.getAttachedEntity()
						&& effect.value.getAttachedEntity() instanceof Player player
						&& player.getEffectHelper().isInvisible()
						&& !HadalGame.usm.isOwnTeam(player.getUser())) {
					effect.key.update(delta);
				} else {
					effect.key.draw(batch, delta);
				}
			} else {
				//this ensures we run update even for culled particles; necessary for setting emitter location
				effect.key.update(delta);
			}
		}
	}

	/**
	 * This draws all particles of this specific particle type. (if visible)
	 * This is run separately to account for particles with additive blending to minimize batch setBlendFunction usage
	 */
	public static void drawParticlesAbove(SpriteBatch batch, float delta) {
		for (Particle effect : ADDITIVE_PARTICLES_TOP) {
			effect.drawEffects(batch, delta);
		}
		batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		for (Particle effect : NORMAL_PARTICLES_TOP) {
			effect.drawEffects(batch, delta);
		}
	}

	public static void drawParticlesBelow(SpriteBatch batch, float delta) {
		for (Particle effect : ADDITIVE_PARTICLES_BOTTOM) {
			effect.drawEffects(batch, delta);
		}
		batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		for (Particle effect : NORMAL_PARTICLES_BOTTOM) {
			effect.drawEffects(batch, delta);
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

	private String getParticleFileName(String filename) {
		return "particles/" + filename + ".particle";
	}

	private static final Array<Particle> NORMAL_PARTICLES_BOTTOM = new Array<>();
	private static final Array<Particle> ADDITIVE_PARTICLES_BOTTOM = new Array<>();
	private static final Array<Particle> NORMAL_PARTICLES_TOP = new Array<>();
	private static final Array<Particle> ADDITIVE_PARTICLES_TOP = new Array<>();
	static {
		for (Particle p : Particle.values()) {
			if (p.additive) {
				if (p.bottom) {
					ADDITIVE_PARTICLES_BOTTOM.add(p);
				} else {
					ADDITIVE_PARTICLES_TOP.add(p);
				}
			} else {
				if (p.bottom) {
					NORMAL_PARTICLES_BOTTOM.add(p);
				} else {
					NORMAL_PARTICLES_TOP.add(p);
				}
			}
		}
	}

	private enum ParticleType {
		MISC,
		DEFAULT,
	}
}
