package com.mygdx.hadal.battle;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.joints.WeldJointDef;
import com.mygdx.hadal.actors.ChatWheel;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.effects.HadalColor;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.Loadout;
import com.mygdx.hadal.event.Scrap;
import com.mygdx.hadal.schmucks.SyncType;
import com.mygdx.hadal.schmucks.UserDataType;
import com.mygdx.hadal.schmucks.entities.HadalEntity;
import com.mygdx.hadal.schmucks.entities.ParticleEntity;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.entities.hitboxes.RangedHitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.server.AlignmentFilter;
import com.mygdx.hadal.states.ClientState;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.HitboxStrategy;
import com.mygdx.hadal.strategies.hitbox.*;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.Stats;
import com.mygdx.hadal.utils.WorldUtil;

import static com.mygdx.hadal.utils.Constants.PPM;

/**
 * This util contains several shortcuts for hitbox-spawning effects for weapons or other items.
 * Includes create explosion, missiles, homing missiles, grenades and bees.
 * @author Lotticelli Lamhock
 */
public class WeaponUtils {

	private static final float SELF_DAMAGE_REDUCTION = 0.5f;
	private static final float EXPLOSION_SPRITE_SCALE = 1.5f;
	private static final Sprite BOOM_SPRITE = Sprite.BOOM;
	public static void createExplosion(PlayState state, Vector2 startPos, float size, Schmuck user, float explosionDamage,
									   float explosionKnockback, short filter, boolean synced, DamageSource source) {
		
		float newSize = size * (1 + user.getBodyData().getStat(Stats.EXPLOSION_SIZE));

		//this prevents players from damaging allies with explosives in the hub
		short actualFilter = filter;
		if (user.getHitboxfilter() == Constants.PLAYER_HITBOX && state.getMode().isHub()) {
			actualFilter = Constants.PLAYER_HITBOX;
		}

		Hitbox hbox = new Hitbox(state, startPos, new Vector2(newSize, newSize), 0.4f, new Vector2(),
			actualFilter, true, false, user, BOOM_SPRITE);
		hbox.setSyncDefault(synced);

		hbox.setSpriteSize(new Vector2(newSize, newSize).scl(EXPLOSION_SPRITE_SCALE));
		hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
		hbox.addStrategy(new Static(state, hbox, user.getBodyData()));
		hbox.addStrategy(new ExplosionDefault(state, hbox, user.getBodyData(), explosionDamage, explosionKnockback,
				SELF_DAMAGE_REDUCTION, source, DamageTag.EXPLOSIVE));

		if (!state.isServer()) {
			((ClientState) state).addEntity(hbox.getEntityID(), hbox, false, ClientState.ObjectLayer.HBOX);
		}
	}

	private static final Vector2 BOMB_SPRITE_SIZE = new Vector2(60, 141);
	private static final Vector2 BOMB_SIZE = new Vector2(60, 60);
	private static final float BOMB_LIFESPAN = 3.0f;

	public static final float BOMB_EXPLOSION_DAMAGE = 40.0f;
	private static final int BOMB_EXPLOSION_RADIUS = 150;
	private static final float BOMB_EXPLOSION_KNOCKBACK = 25.0f;
	private static final Sprite BOMB_SPRITE = Sprite.BOMB;
	private static final Sprite SPARK_SPRITE = Sprite.SPARKS;

	public static Hitbox createBomb(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, DamageSource source) {
		SoundEffect.LAUNCHER.playSourced(state, user.getPixelPosition(), 0.2f);

		Hitbox hbox = new RangedHitbox(state, startPosition, BOMB_SIZE, BOMB_LIFESPAN, startVelocity, user.getHitboxfilter(),
				false, true, user, BOMB_SPRITE);
		hbox.setSpriteSize(BOMB_SPRITE_SIZE);
		hbox.setGravity(2.5f);
		hbox.setRestitution(0.5f);

		hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
		hbox.addStrategy(new DropThroughPassability(state, hbox, user.getBodyData()));
		hbox.addStrategy(new ContactUnitDie(state, hbox, user.getBodyData()));
		hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), 0, 0, source,
				DamageTag.EXPLOSIVE, DamageTag.RANGED));
		hbox.addStrategy(new DieExplode(state, hbox, user.getBodyData(), BOMB_EXPLOSION_RADIUS, BOMB_EXPLOSION_DAMAGE, BOMB_EXPLOSION_KNOCKBACK,
				(short) 0, false, source));
		hbox.addStrategy(new DieSound(state, hbox, user.getBodyData(), SoundEffect.BOMB, 0.4f).setSynced(false));
		hbox.addStrategy(new ContactWallSound(state, hbox, user.getBodyData(), SoundEffect.WALL_HIT1, 0.2f).setSynced(false));
		hbox.addStrategy(new FlashShaderNearDeath(state, hbox, user.getBodyData(), 1.0f, false));

		Hitbox sparks = new RangedHitbox(state, startPosition, BOMB_SIZE, BOMB_LIFESPAN, startVelocity, user.getHitboxfilter(),
				true, false, user, SPARK_SPRITE);
		sparks.setSpriteSize(BOMB_SPRITE_SIZE);
		sparks.setSyncDefault(false);

		sparks.addStrategy(new ControllerDefault(state, sparks, user.getBodyData()));
		sparks.addStrategy(new FixedToEntity(state, sparks, user.getBodyData(), hbox, new Vector2(), new Vector2()));

		if (!state.isServer()) {
			((ClientState) state).addEntity(sparks.getEntityID(), sparks, false, ClientState.ObjectLayer.HBOX);
		}

		return hbox;
	}

	public static final float TORPEDO_EXPLOSION_DAMAGE = 18.0f;
	private static final float TORPEDO_BASE_DAMAGE = 3.0f;
	private static final float TORPEDO_EXPLOSION_KNOCKBACK = 16.0f;
	private static final float TORPEDO_BASE_KNOCKBACK = 3.0f;
	private static final int TORPEDO_EXPLOSION_RADIUS = 150;
	private static final Vector2 TORPEDO_SIZE = new Vector2(60, 14);
	private static final float TORPEDO_LIFESPAN = 8.0f;
	private static final int TORPEDO_SPREAD = 30;
	private static final float TORPEDO_HOMING = 100;
	private static final int TORPEDO_HOMING_RADIUS = 100;
	private static final Sprite MISSILE_SPRITE = Sprite.MISSILE_B;
	public static Hitbox createHomingMissile(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity,
											 DamageSource source) {
		Hitbox hbox = new RangedHitbox(state, startPosition, TORPEDO_SIZE, TORPEDO_LIFESPAN, startVelocity, user.getHitboxfilter(),
				true, false, user, MISSILE_SPRITE);

		hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
		hbox.addStrategy(new AdjustAngle(state, hbox, user.getBodyData()));
		hbox.addStrategy(new ContactUnitDie(state, hbox, user.getBodyData()));
		hbox.addStrategy(new ContactWallDie(state, hbox, user.getBodyData()));
		hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), TORPEDO_BASE_DAMAGE, TORPEDO_BASE_KNOCKBACK,
				source, DamageTag.EXPLOSIVE, DamageTag.RANGED));
		hbox.addStrategy(new DieExplode(state, hbox, user.getBodyData(), TORPEDO_EXPLOSION_RADIUS, TORPEDO_EXPLOSION_DAMAGE,
				TORPEDO_EXPLOSION_KNOCKBACK, user.getHitboxfilter(), false, source));
		hbox.addStrategy(new HomingUnit(state, hbox, user.getBodyData(), TORPEDO_HOMING, TORPEDO_HOMING_RADIUS));
		hbox.addStrategy(new Spread(state, hbox, user.getBodyData(), TORPEDO_SPREAD));
		hbox.addStrategy(new DieSound(state, hbox, user.getBodyData(), SoundEffect.EXPLOSION6, 0.25f).setSynced(false));
		hbox.addStrategy(new FlashShaderNearDeath(state, hbox, user.getBodyData(), 1.0f, false));

		return hbox;
	}

	public static final float NAUTICAL_MINE_LIFESPAN = 12.0f;
	public static final float NAUTICAL_MINE_EXPLOSION_DAMAGE = 75.0f;
	private static final float PRIME_DELAY = 1.0f;
	private static final float PROJ_DAMPEN = 1.0f;
	private static final Vector2 NAUTICAL_MINE_SIZE = new Vector2(120, 120);
	private static final int NAUTICAL_MINE_EXPLOSION_RADIUS = 400;
	private static final float NAUTICAL_MINE_EXPLOSION_KNOCKBACK = 40.0f;
	private static final float FOOTBALL_THRESHOLD = 200.0f;
	private static final float FOOTBALL_DEPRECIATION = 50.0f;
	public static Hitbox createNauticalMine(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, float[] extraFields) {
		SoundEffect.LAUNCHER.playSourced(state, user.getPixelPosition(), 1.0f);

		boolean event = false;
		float pushMultiplier = 1.0f;
		float lifespan = NAUTICAL_MINE_LIFESPAN;
		if (extraFields.length > 2) {
			event = extraFields[0] == 0.0f;
			pushMultiplier = extraFields[1];
			lifespan = extraFields[2];
		}

		Hitbox hbox = new RangedHitbox(state, startPosition, NAUTICAL_MINE_SIZE, lifespan, startVelocity,
				(short) 0, false, false, user, Sprite.NAVAL_MINE);
		hbox.setRestitution(0.5f);

		hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
		if (event) {
			hbox.addStrategy(new ContactGoalScore(state, hbox, user.getBodyData()));
			hbox.addStrategy(new DamageThresholdDie(state, hbox, user.getBodyData(), FOOTBALL_THRESHOLD, FOOTBALL_DEPRECIATION));
		} else {
			hbox.addStrategy(new ContactUnitDie(state, hbox, user.getBodyData()).setDelay(PRIME_DELAY));
		}

		hbox.addStrategy(new AdjustAngle(state, hbox, user.getBodyData()));
		hbox.addStrategy(new Pushable(state, hbox, user.getBodyData(), pushMultiplier));
		hbox.addStrategy(new DieExplode(state, hbox, user.getBodyData(), NAUTICAL_MINE_EXPLOSION_RADIUS, NAUTICAL_MINE_EXPLOSION_DAMAGE,
				NAUTICAL_MINE_EXPLOSION_KNOCKBACK, (short) 0, false, DamageSource.NAUTICAL_MINE));
		hbox.addStrategy(new DieSound(state, hbox, user.getBodyData(), SoundEffect.EXPLOSION_FUN, 0.4f).setSynced(false));
		hbox.addStrategy(new FlashShaderNearDeath(state, hbox, user.getBodyData(), 1.0f, false));
		hbox.addStrategy(new HitboxStrategy(state, hbox, user.getBodyData()) {

			@Override
			public void create() {
				super.create();
				hbox.getBody().setLinearDamping(PROJ_DAMPEN);
			}
		});

		return hbox;
	}

	public static final float STICK_GRENADE_EXPLOSION_DAMAGE = 28.0f;
	private static final Vector2 STICK_GRENADE_SIZE = new Vector2(19, 70);
	private static final float STICK_GRENADE_LIFESPAN = 3.0f;
	private static final float STICK_GRENADE_BASE_DAMAGE = 8.0f;
	private static final float STICK_GRENADE_BASE_KNOCKBACK = 3.0f;
	private static final float STICK_GRENADE_EXPLOSION_KNOCKBACK = 12.0f;
	private static final int STICK_GRENADE_EXPLOSION_RADIUS = 100;
	private static final float GRENADE_ROTATION_SPEED = 8.0f;
	public static Hitbox createStickGrenade(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity) {
		SoundEffect.LAUNCHER.playSourced(state, user.getPixelPosition(), 1.0f);

		Hitbox hbox = new RangedHitbox(state, startPosition, STICK_GRENADE_SIZE, STICK_GRENADE_LIFESPAN, startVelocity,
				user.getHitboxfilter(), false, false, user, Sprite.CABER);

		hbox.setGravity(1.0f);

		hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
		hbox.addStrategy(new RotationConstant(state, hbox, user.getBodyData(), GRENADE_ROTATION_SPEED));
		hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), STICK_GRENADE_BASE_DAMAGE, STICK_GRENADE_BASE_KNOCKBACK,
				DamageSource.CRIME_DISCOURAGEMENT_STICK, DamageTag.EXPLOSIVE));
		hbox.addStrategy(new ContactWallDie(state, hbox, user.getBodyData()));
		hbox.addStrategy(new ContactUnitDie(state, hbox, user.getBodyData()));
		hbox.addStrategy(new DieExplode(state, hbox, user.getBodyData(), STICK_GRENADE_EXPLOSION_RADIUS, STICK_GRENADE_EXPLOSION_DAMAGE,
				STICK_GRENADE_EXPLOSION_KNOCKBACK, user.getHitboxfilter(), false, DamageSource.CRIME_DISCOURAGEMENT_STICK));
		return hbox;
	}

	public static final float PRIME_TIME = 1.0f;
	private static final Vector2 MINE_SIZE = new Vector2(75, 30);
	private static final float WARNING_TIME = 0.5f;
	private static final float MINE_SPEED = 60.0f;
	private static final float MINE_LIFESPAN = 18.0f;
	private static final int MINE_EXPLOSION_RADIUS = 270;
	private static final float MINE_DEFAULT_DAMAGE = 100.0f;
	private static final float MINE_EXPLOSION_KNOCKBACK = 50.0f;
	private static final float MINE_TARGET_CHECK_CD = 0.2f;
	private static final float MINE_TARGET_CHECK_RADIUS = 3.6f;
	public static Hitbox createProximityMine(PlayState state, Schmuck user, Vector2 startPosition,
											 DamageSource source, float[] extraFields) {

		final float mineDamage = extraFields.length > 0 ? extraFields[0] : MINE_DEFAULT_DAMAGE;

		final boolean[] primed = new boolean[] { false };
		Hitbox hbox = new RangedHitbox(state, startPosition, MINE_SIZE, MINE_LIFESPAN, new Vector2(0, -MINE_SPEED),
				(short) 0, false, false, user, Sprite.LAND_MINE) {

			@Override
			public void render(SpriteBatch batch) {
				if (!alive) { return; }
				if (primed[0]) { return; }
				super.render(batch);
			}
		};
		hbox.setPassability((short) (Constants.BIT_WALL | Constants.BIT_DROPTHROUGHWALL | Constants.BIT_PLAYER));
		hbox.makeUnreflectable();
		hbox.setGravity(3.0f);
		hbox.setSyncDefault(false);

		hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
		hbox.addStrategy(new DropThroughPassability(state, hbox, user.getBodyData()));
		hbox.addStrategy(new HitboxStrategy(state, hbox, user.getBodyData()) {

			private HadalEntity floor;
			private boolean planted, set;
			private float primeCount;
			private float targetCheckCount;
			private final Vector2 mineLocation = new Vector2();
			@Override
			public void onHit(HadalData fixB) {
				if (fixB != null) {
					if (UserDataType.WALL.equals(fixB.getType()) || UserDataType.EVENT.equals(fixB.getType())) {
						floor = fixB.getEntity();
						if (floor != null) {
							if (floor.getBody() != null) {
								planted = true;
							}
						}
					}
				}
			}

			@Override
			public void controller(float delta) {
				if (planted && floor.getBody() != null && hbox.getBody() != null) {
					planted = false;
					WeldJointDef joint = new WeldJointDef();
					joint.bodyA = floor.getBody();
					joint.bodyB = hbox.getBody();
					joint.localAnchorA.set(new Vector2(hbox.getPosition()).sub(floor.getPosition()));
					joint.localAnchorB.set(0, 0);
					state.getWorld().createJoint(joint);

					SoundEffect.SLAP.playSourced(state, hbox.getPixelPosition(), 0.6f);
					set = true;
				}
				if (set && !primed[0]) {
					primeCount += delta;
					if (primeCount >= PRIME_TIME) {
						SoundEffect.MAGIC27_EVIL.playSourced(state, hbox.getPixelPosition(), 1.0f);
						primed[0] = true;
						hbox.setLifeSpan(MINE_LIFESPAN);

						ParticleEntity particles = new ParticleEntity(state, new Vector2(hbox.getPixelPosition()), Particle.SMOKE, 1.0f,
								true, SyncType.NOSYNC).setScale(0.5f);

						if (!state.isServer()) {
							((ClientState) state).addEntity(particles.getEntityID(), particles, false, ClientState.ObjectLayer.HBOX);
						}
					}
				}
				if (primed[0]) {
					if (targetCheckCount < MINE_TARGET_CHECK_CD) {
						targetCheckCount += delta;
					}
					if (targetCheckCount >= MINE_TARGET_CHECK_CD) {
						targetCheckCount -= MINE_TARGET_CHECK_CD;
						mineLocation.set(hbox.getPosition());
						state.getWorld().QueryAABB(fixture -> {
							if (fixture.getUserData() instanceof BodyData) {
								hbox.die();
							}
							return true;
						},
						mineLocation.x - MINE_TARGET_CHECK_RADIUS, mineLocation.y - MINE_TARGET_CHECK_RADIUS,
						mineLocation.x + MINE_TARGET_CHECK_RADIUS, mineLocation.y + MINE_TARGET_CHECK_RADIUS);
					}
				}
			}

			@Override
			public void die() {
				SoundEffect.PING.playSourced(state, hbox.getPixelPosition(), 0.6f, 1.5f);
				Hitbox explosion = new RangedHitbox(state, hbox.getPixelPosition(), MINE_SIZE, WARNING_TIME,  new Vector2(),
						(short) 0, true, false, user, Sprite.LAND_MINE);
				explosion.makeUnreflectable();
				explosion.setSyncDefault(false);

				explosion.addStrategy(new ControllerDefault(state, explosion, user.getBodyData()));
				explosion.addStrategy(new Static(state, explosion, user.getBodyData()));
				explosion.addStrategy(new FlashShaderNearDeath(state, explosion, user.getBodyData(), WARNING_TIME, false));
				explosion.addStrategy(new DieExplode(state, explosion, user.getBodyData(), MINE_EXPLOSION_RADIUS, mineDamage,
						MINE_EXPLOSION_KNOCKBACK, (short) 0, false, source));
				explosion.addStrategy(new DieSound(state, explosion, user.getBodyData(), SoundEffect.EXPLOSION6, 0.6f).setSynced(false));

				if (!state.isServer()) {
					((ClientState) state).addEntity(explosion.getEntityID(), explosion, false, ClientState.ObjectLayer.HBOX);
				}
			}
		});

		return hbox;
	}

	public static final float SPIRIT_DEFAULT_DAMAGE = 45.0f;
	private static final int SPIRIT_SIZE = 25;
	private static final float SPIRIT_HOMING = 120;
	private static final int SPIRIT_HOMING_RADIUS = 40;
	private static final float SPIRIT_KNOCKBACK = 25.0f;
	private static final float SPIRIT_LIFESPAN = 7.5f;
	public static Hitbox[] createVengefulSpirits(PlayState state, Schmuck user, Vector2[] startPosition,
												 DamageSource source, float[] extraFields) {
		Hitbox[] hboxes = new Hitbox[startPosition.length];

		boolean attached = true;
		float spiritDamage = SPIRIT_DEFAULT_DAMAGE;
		Particle effect = Particle.SHADOW_PATH;
		if (extraFields.length > 3) {
			attached = extraFields[0] == 1.0f;
			if (extraFields[1] == 1.0f) {
				effect = Particle.BRIGHT;
			}
			spiritDamage = extraFields[2];
		}

		if (startPosition.length != 0) {
			SoundEffect.DARKNESS2.playSourced(state, user.getPixelPosition(), 0.2f);

			for (int i = 0; i < startPosition.length; i++) {
				Hitbox hbox = new RangedHitbox(state, startPosition[i], new Vector2(SPIRIT_SIZE, SPIRIT_SIZE), SPIRIT_LIFESPAN,
						new Vector2(), user.getHitboxfilter(), true, true, user, Sprite.NOTHING);

				hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
				hbox.addStrategy(new ContactUnitDie(state, hbox, user.getBodyData()));
				hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), spiritDamage, SPIRIT_KNOCKBACK,
						source, DamageTag.MAGIC, DamageTag.RANGED));

				//attached hboxes will follow the player until they have a target to home in on
				if (attached) {
					hbox.addStrategy(new HomingUnit(state, hbox, user.getBodyData(), SPIRIT_HOMING, SPIRIT_HOMING_RADIUS)
							.setFixedUntilHome(true).setTarget(user));
				} else {
					hbox.addStrategy(new HomingUnit(state, hbox, user.getBodyData(), SPIRIT_HOMING, SPIRIT_HOMING_RADIUS));
				}
				hbox.addStrategy(new CreateParticles(state, hbox, user.getBodyData(), effect, 0.0f, 1.0f)
						.setParticleColor(HadalColor.RANDOM).setSyncType(SyncType.NOSYNC));
				hbox.addStrategy(new DieSound(state, hbox, user.getBodyData(), SoundEffect.DARKNESS1, 0.25f).setSynced(false));

				hboxes[i] = hbox;
			}
		}
		return hboxes;
	}
	
	public static void createExplodingReticle(PlayState state, Vector2 startPos, Schmuck user, float reticleSize,
											  float reticleLifespan, float explosionDamage, float explosionKnockback,
											  int explosionRadius, DamageSource source) {
		Hitbox hbox = new RangedHitbox(state, startPos, new Vector2(reticleSize, reticleSize), reticleLifespan,
			new Vector2(), user.getHitboxfilter(), true, false, user, Sprite.CROSSHAIR);
		hbox.setPassability((short) (Constants.BIT_PROJECTILE | Constants.BIT_WALL | Constants.BIT_PLAYER | Constants.BIT_ENEMY));
		
		hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
		hbox.addStrategy(new CreateParticles(state, hbox, user.getBodyData(), Particle.EVENT_HOLO, 0.0f, 1.0f).setParticleSize(40.0f).setParticleColor(
			HadalColor.HOT_PINK));
		hbox.addStrategy(new DieExplode(state, hbox, user.getBodyData(), explosionRadius, explosionDamage, explosionKnockback,
				user.getHitboxfilter(), true, source));
		hbox.addStrategy(new DieSound(state, hbox, user.getBodyData(), SoundEffect.EXPLOSION6, 0.25f));
		hbox.addStrategy(new Static(state, hbox, user.getBodyData()));
	}
	
	private static final Sprite[] PROJ_SPRITES = {Sprite.METEOR_A, Sprite.METEOR_B, Sprite.METEOR_C, Sprite.METEOR_D,
			Sprite.METEOR_E, Sprite.METEOR_F};
	private static final Vector2 METEOR_SIZE = new Vector2(75, 75);
	private static final float METEOR_SPEED = 50.0f;
	private static final float RANGE = 1500.0f;
	private static final float LIFESPAN = 5.0f;
	
	public static void createMeteors(PlayState state, Vector2 startPos, Schmuck user, float meteorDuration, float meteorInterval,
									 float spread, float baseDamage, DamageSource source) {
		Hitbox hbox = new RangedHitbox(state, startPos, new Vector2(1, 1), meteorDuration, new Vector2(),
			(short) 0, false, false, user, Sprite.NOTHING);
		hbox.makeUnreflectable();
		hbox.setSyncDefault(false);
		
		hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
		hbox.addStrategy(new Static(state, hbox, user.getBodyData()));
		hbox.addStrategy(new HitboxStrategy(state, hbox, user.getBodyData()) {
			
			private float shortestFraction;
			private final Vector2 originPt = new Vector2();
			private final Vector2 endPt = new Vector2();
			
			private float procCdCount;
			private float meteorCount;
			@Override
			public void controller(float delta) {
				procCdCount += delta;

				if (procCdCount >= meteorInterval) {
					procCdCount -= meteorInterval;

					meteorCount++;
					
					if (meteorCount % 3 == 0) {
						hbox.addStrategy(new CreateSound(state, hbox, user.getBodyData(), SoundEffect.FALLING, 0.5f, false));
					}
					
					originPt.set(startPos).add((MathUtils.random() -  0.5f) * spread, 0);
					endPt.set(originPt).add(0, -RANGE);
					shortestFraction = 1.0f;

					if (WorldUtil.preRaycastCheck(originPt, endPt)) {
						state.getWorld().rayCast((fixture, point, normal, fraction) -> {
							if (fixture.getFilterData().categoryBits == Constants.BIT_WALL && fraction < shortestFraction) {
								shortestFraction = fraction;
								return fraction;
						}
						return -1.0f;
						}, originPt, endPt);
					}
					
					endPt.set(originPt).add(0, -RANGE * shortestFraction).scl(PPM);
					originPt.set(endPt).add(0, RANGE);
					
					int randomIndex = MathUtils.random(PROJ_SPRITES.length - 1);
					Sprite projSprite = PROJ_SPRITES[randomIndex];

					Hitbox hbox = new Hitbox(state, new Vector2(originPt), METEOR_SIZE, LIFESPAN, new Vector2(0, -METEOR_SPEED), user.getHitboxfilter(), true, false, user, projSprite);
					hbox.setPassability((short) (Constants.BIT_PROJECTILE | Constants.BIT_WALL | Constants.BIT_PLAYER | Constants.BIT_ENEMY));

					hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
					hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), baseDamage, 0.0f,
							source, DamageTag.FIRE, DamageTag.MAGIC));
					hbox.addStrategy(new HitboxStrategy(state, hbox, user.getBodyData()) {
						
						private final Vector2 floor = new Vector2(endPt);
						@Override
						public void controller(float delta) {
							if (hbox.getPixelPosition().y - hbox.getSize().y / 2 <= floor.y) {
								hbox.setLinearVelocity(0, 0);
								hbox.die();
							}
						}
					});
					
					hbox.addStrategy(new CreateParticles(state, hbox, user.getBodyData(), Particle.FIRE, 0.0f, 1.0f));
					hbox.addStrategy(new DieParticles(state, hbox, user.getBodyData(), Particle.BOULDER_BREAK).setParticleSize(90));
				}
			}
		});
	}

	private static final Sprite[] VINE_SPRITES = {Sprite.VINE_A, Sprite.VINE_C, Sprite.VINE_D};
	public static void createVine(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelo,
								  int vineNum, float lifespan, float vineDamage, float vineKB, int spreadMin,
								  int spreadMax, int bendLength, int bendSpread,  Vector2 vineInvisSize,
								  Vector2 vineSize, Vector2 vineSpriteSize, int splitNum) {
		SoundEffect.ATTACK1.playUniversal(state, user.getPixelPosition(), 0.4f, 0.5f, false);

		//create an invisible hitbox that makes the vines as it moves
		RangedHitbox hbox = new RangedHitbox(state, startPosition, vineInvisSize, lifespan, startVelo, user.getHitboxfilter(),
			false, false, user, Sprite.NOTHING);
		hbox.setSyncDefault(false);
		hbox.makeUnreflectable();
		hbox.setRestitution(1.0f);

		hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
		hbox.addStrategy(new HitboxStrategy(state, hbox, user.getBodyData()) {

			private final Vector2 lastPosition = new Vector2();
			private final Vector2 lastPositionTemp = new Vector2();
			private final Vector2 entityLocation = new Vector2();
			private int vineCount, vineCountTotal, nextBend;
			private boolean bendRight;
			private float displacement;
			private final Vector2 angle = new Vector2();
			@Override
			public void controller(float delta) {
				entityLocation.set(hbox.getPixelPosition());

				displacement += lastPosition.dst(entityLocation);
				lastPositionTemp.set(lastPosition);
				lastPosition.set(entityLocation);

				//after moving distance equal to a vine, the hbox spawns a vine with random sprite
				if (displacement > vineSize.x) {
					if (lastPositionTemp.isZero()) {
						lastPosition.set(entityLocation);
					} else {
						lastPosition.add(new Vector2(lastPosition).sub(lastPositionTemp).nor().scl((displacement - vineSize.x) / PPM));
					}
					displacement = 0.0f;

					int randomIndex = MathUtils.random(VINE_SPRITES.length - 1);
					Sprite projSprite = VINE_SPRITES[randomIndex];

					RangedHitbox vine = new RangedHitbox(state, lastPosition, vineSize, lifespan, new Vector2(),
						user.getHitboxfilter(), true, true, creator.getSchmuck(),
						vineCountTotal == vineNum && splitNum == 0 ? Sprite.VINE_B : projSprite) {

						private final Vector2 newPosition = new Vector2();
						@Override
						public void create() {
							super.create();

							//vines match hbox velocity but are drawn at an offset so they link together better
							float newAngle = MathUtils.atan2(hbox.getLinearVelocity().y , hbox.getLinearVelocity().x);
							newPosition.set(getPosition()).add(new Vector2(hbox.getLinearVelocity()).nor().scl(vineSize.x / 2 / PPM));
							setTransform(newPosition.x, newPosition.y, newAngle);
						}
					};
					vine.setSpriteSize(vineSpriteSize);
					vine.setEffectsMovement(false);

					vine.addStrategy(new ControllerDefault(state, vine, user.getBodyData()));
					vine.addStrategy(new ContactUnitSound(state, vine, user.getBodyData(), SoundEffect.STAB, 0.6f, true));
					vine.addStrategy(new DamageStandard(state, vine, user.getBodyData(), vineDamage, vineKB,
							DamageSource.ENEMY_ATTACK , DamageTag.RANGED).setStaticKnockback(true));
					vine.addStrategy(new CreateParticles(state, vine, user.getBodyData(), Particle.DANGER_RED, 0.0f, 1.0f).setParticleSize(90.0f));
					vine.addStrategy(new DieParticles(state, vine, user.getBodyData(), Particle.PLANT_FRAG));
					vine.addStrategy(new Static(state, vine, user.getBodyData()));

					vineCount++;
					vineCountTotal++;
					if (vineCount >= nextBend) {

						//hbox's velocity changes randomly to make vine wobble
						hbox.setLinearVelocity(hbox.getLinearVelocity().rotateDeg((bendRight ? -1 : 1) * MathUtils.random(spreadMin, spreadMax)));
						bendRight = !bendRight;
						vineCount = 0;
						nextBend = bendLength + (MathUtils.random(-bendSpread, bendSpread + 1));
					}
					if (vineCountTotal > vineNum) {
						hbox.die();
					}
				}
			}

			@Override
			public void die() {

				if (splitNum > 0) {
					//when vine dies, it creates 2 vines that branch in separate directions
					float newDegrees = hbox.getLinearVelocity().angleDeg() + (MathUtils.random(spreadMin, spreadMax));
					angle.set(hbox.getLinearVelocity()).setAngleDeg(newDegrees);
					WeaponUtils.createVine(state, user, hbox.getPixelPosition(), angle, vineNum, lifespan,
						vineDamage, vineKB, spreadMin, spreadMax, 2, 1,
						vineInvisSize, vineSize, vineSpriteSize, splitNum - 1);

					newDegrees = hbox.getLinearVelocity().angleDeg() - (MathUtils.random(spreadMin, spreadMax));
					angle.set(hbox.getLinearVelocity()).setAngleDeg(newDegrees);
					WeaponUtils.createVine(state, user, hbox.getPixelPosition(), angle, vineNum, lifespan,
						vineDamage, vineKB, spreadMin, spreadMax, 2, 1,
						vineInvisSize, vineSize, vineSpriteSize, splitNum - 1);
				}
			}
		});
	}

	private static final Vector2 PING_SIZE = new Vector2(60, 54);
	private static final Vector2 PING_ARROW_SIZE = new Vector2(60, 33);
	private static final Vector2 PING_OFFSET = new Vector2(0, -40);
	private static final float PING_LIFESPAN = 2.0f;
	public static Hitbox createPing(PlayState state, Schmuck user, Vector2 startPosition) {
		SoundEffect.PING.playSourced(state, startPosition, 0.6f);

		Hitbox hbox = new RangedHitbox(state, startPosition, PING_SIZE, PING_LIFESPAN, new Vector2(),
				user.getHitboxfilter(), true, false, user, Sprite.NOTIFICATIONS_ALERT);

		hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
		hbox.addStrategy(new Static(state, hbox, user.getBodyData()));

		Hitbox hboxPing = new RangedHitbox(state, new Vector2(startPosition).add(PING_OFFSET), PING_ARROW_SIZE, PING_LIFESPAN, new Vector2(),
				user.getHitboxfilter(), true, false, user, Sprite.NOTIFICATIONS_ALERT_PING);
		hboxPing.setSpriteSize(PING_ARROW_SIZE);
		hboxPing.setSyncDefault(false);

		hboxPing.addStrategy(new ControllerDefault(state, hboxPing, user.getBodyData()));
		hboxPing.addStrategy(new Static(state, hboxPing, user.getBodyData()));

		if (!state.isServer()) {
			((ClientState) state).addEntity(hboxPing.getEntityID(), hboxPing, false, ClientState.ObjectLayer.HBOX);
		}

		return hbox;
	}

	public static final float EMOTE_EXPLODE_DAMAGE = 90.0f;
	private static final Vector2 EMOTE_SIZE = new Vector2(64, 64);
	private static final float EMOTE_LIFESPAN = 1.9f;
	private static final float EMOTE_LIFESPAN_LONG = 6.0f;
	private static final int EMOTE_EXPLODE_RADIUS = 150;
	private static final float EMOTE_EXPLODE_KNOCKBACK = 20;

	public static Hitbox createEmote(PlayState state, Schmuck user, float[] extraFields) {

		boolean special = user.getBodyData().getStat(Stats.PING_DAMAGE) > 0.0f;
		int spriteIndex = 0;
		if (extraFields.length >= 1) {
			spriteIndex = (int) extraFields[0];
		}

		Sprite emote = ChatWheel.indexToEmote(spriteIndex);

		Hitbox hbox = new RangedHitbox(state, new Vector2(user.getPixelPosition()).add(0, user.getSize().y / 2 + 50), EMOTE_SIZE,
			special ? EMOTE_LIFESPAN_LONG : EMOTE_LIFESPAN, new Vector2(), (short) 0, !special, special, user, emote);

		hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
		hbox.addStrategy(new HitboxStrategy(state, hbox, user.getBodyData()) {

			private float controllerCount;
			private final Vector2 entityLocation = new Vector2();
			@Override
			public void controller(float delta) {

				//non-special emotes despawn if the user dies
				if (!user.isAlive() && !special) {
					hbox.die();
				} else {
					controllerCount += delta;

					if (controllerCount <= EMOTE_LIFESPAN) {
						entityLocation.set(user.getPosition()).add(0, (user.getSize().y / 2 + 50) / PPM);
						hbox.setTransform(entityLocation, hbox.getAngle());
						hbox.setLinearVelocity(user.getLinearVelocity());
					}
				}
			}
		});

		//with the Finger equipped, emotes detach and explode
		if (special) {
			hbox.setRestitution(0.5f);
			hbox.addStrategy(new Pushable(state, hbox, user.getBodyData(), 1.0f));
			hbox.addStrategy(new ContactUnitDie(state, hbox, user.getBodyData()).setDelay(EMOTE_LIFESPAN + 1.0f));
			hbox.addStrategy(new DieExplode(state, hbox, user.getBodyData(), EMOTE_EXPLODE_RADIUS, EMOTE_EXPLODE_DAMAGE,
					EMOTE_EXPLODE_KNOCKBACK, (short) 0, false, DamageSource.THE_FINGER));
			hbox.addStrategy(new DieSound(state, hbox, user.getBodyData(), SoundEffect.EXPLOSION_FUN, 0.4f).setSynced(false));
			hbox.addStrategy(new FlashShaderNearDeath(state, hbox, user.getBodyData(), 1.0f, false));
			hbox.addStrategy(new HitboxStrategy(state, hbox, user.getBodyData()) {

				@Override
				public void create() {
					super.create();
					hbox.getBody().setLinearDamping(PROJ_DAMPEN);
				}
			});
		}

		return hbox;
	}

	/**
	 * This method returns a player's "color" corresponding to their team color or their character with no team.
	 * This is used to color code player name as well as for streak particle coloring
	 */
	public static Vector3 getPlayerColor(Player player) {

		//return empty vector if player's data has not been created yet.
		if (player.getPlayerData() != null) {
			Loadout loadout = player.getPlayerData().getLoadout();
			if (AlignmentFilter.NONE.equals(loadout.team)) {
				return loadout.character.getPalette().getIcon().getRGB();
			} else if (loadout.team.getPalette().getIcon().getRGB().isZero()) {
				return loadout.character.getPalette().getIcon().getRGB();
			} else {
				return loadout.team.getPalette().getIcon().getRGB();
			}
		} else {
			return new Vector3();
		}
	}

	private static final Vector3 rgb = new Vector3();
	/**
	 * This returns a string corresponding to a player's colored name. (optionally abridged)
	 * Used for kill feed messages and chat window names.
	 */
	public static String getPlayerColorName(Schmuck schmuck, int maxNameLen) {

		if (schmuck == null) { return ""; }

		if (schmuck instanceof Player player) {
			String displayedName = player.getName();

			if (displayedName.length() > maxNameLen) {
				displayedName = displayedName.substring(0, maxNameLen).concat("...");
			}

			//get the player's color and use color markup to add color tags.
			rgb.set(getPlayerColor(player));
			String hex = "#" + Integer.toHexString(Color.rgb888(rgb.x, rgb.y, rgb.z));
			return "[" + hex + "]" + displayedName + "[]";
		} else {
			return schmuck.getName();
		}
	}

	public static String getColorName(HadalColor color, String name) {
		String hex = "#" + Integer.toHexString(Color.rgb888(color.getColor()));
		return "[" + hex + "]" + name + "[]";
	}

	public static final Vector2 PICKUP_SIZE = new Vector2(40, 40);
	public static final float PICKUP_DURATION = 10.0f;
	private static final float FLASH_LIFESPAN = 1.0f;
	public static Hitbox createPickup(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, float[] extraFields) {

		final int type = extraFields.length >= 1 ? (int) extraFields[0] : 0;
		final float power = extraFields.length >= 2 ? extraFields[1] : 0;
		Sprite sprite = Sprite.NOTHING;
		if (type == Constants.PICKUP_HEALTH) {
			sprite = Sprite.MEDPAK;
		}
		if (type == Constants.PICKUP_FUEL) {
			sprite = Sprite.FUEL;
		}
		if (type == Constants.PICKUP_AMMO) {
			sprite = Sprite.AMMO;
		}

		Hitbox hbox = new RangedHitbox(state, startPosition, PICKUP_SIZE, PICKUP_DURATION, startVelocity,
				(short) 0, false, false, user, sprite);
		hbox.setGravity(1.0f);
		hbox.setFriction(1.0f);

		hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
		hbox.addStrategy(new DropThroughPassability(state, hbox, user.getBodyData()));
		hbox.addStrategy(new FlashNearDeath(state, hbox, user.getBodyData(), FLASH_LIFESPAN));
		hbox.addStrategy(new CreateParticles(state, hbox, user.getBodyData(), Particle.EVENT_HOLO, 0.0f, 1.0f)
				.setSyncType(SyncType.NOSYNC));
		hbox.addStrategy(new HitboxStrategy(state, hbox, user.getBodyData()) {

			//delay prevents spawned medpaks from being instantly consumed by the (dead) player that dropped them
			private float delay = 0.1f;
			@Override
			public void controller(float delta) {
				if (delay >= 0) {
					delay -= delta;
				}
			}

			@Override
			public void onHit(HadalData fixB) {
				if (fixB instanceof PlayerBodyData bodyData && delay <= 0) {
					if (type == Constants.PICKUP_HEALTH) {
						if (bodyData.getCurrentHp() < bodyData.getStat(Stats.MAX_HP)) {

							SoundEffect.MAGIC21_HEAL.playUniversal(state, bodyData.getPlayer().getPixelPosition(),
									0.3f, false);

							bodyData.regainHp(power * bodyData.getStat(Stats.MAX_HP), bodyData, true, DamageTag.MEDPAK);
							new ParticleEntity(state, bodyData.getSchmuck(), Particle.PICKUP_HEALTH, 3.0f,
									5.0f, true, SyncType.CREATESYNC);
							hbox.die();
						}
					}
					if (type == Constants.PICKUP_FUEL) {
						if (bodyData.getCurrentFuel() < bodyData.getStat(Stats.MAX_FUEL)) {

							SoundEffect.MAGIC2_FUEL.playUniversal(state, bodyData.getPlayer().getPixelPosition(),
									0.3f, false);

							bodyData.fuelGain(power);
							new ParticleEntity(state, bodyData.getSchmuck(), Particle.PICKUP_ENERGY, 3.0f,
									5.0f, true, SyncType.CREATESYNC);
							hbox.die();
						}
					}
					if (type == Constants.PICKUP_AMMO) {
						if (bodyData.getCurrentTool().getClipLeft() < bodyData.getCurrentTool().getClipSize()) {
							SoundEffect.LOCKANDLOAD.playUniversal(state, bodyData.getPlayer().getPixelPosition(),
									0.8f, false);

							bodyData.getCurrentTool().gainAmmo(power);
							new ParticleEntity(state, bodyData.getSchmuck(), Particle.PICKUP_ENERGY, 0.0f,
									5.0f, true, SyncType.CREATESYNC);
							hbox.die();
						}
					}
				}
			}
		});
		if (type == Constants.PICKUP_HEALTH) {
			hbox.setBotHealthPickup(true);
		}
		return hbox;
	}
	
	/**
	 * This spawns some amount of scrap events as currency for the player
	 * @param statCheck: do we take into account the player's bonus scrap drop?
	 * @param score: does picking up the screp increment the player's score?
	 */
	public static void spawnScrap(PlayState state, int amount, Vector2 startPos, boolean statCheck, boolean score) {
		
		int modifiedAmount;
		if (statCheck && state.getPlayer().getPlayerData() != null) {
			if (state.getPlayer().getPlayerData().getStat(Stats.EXTRA_SCRAP) * amount < 1.0f
					&& state.getPlayer().getPlayerData().getStat(Stats.EXTRA_SCRAP) > 0) {
				modifiedAmount = amount + 1;
			} else {
				modifiedAmount = (int) (amount * (1 + state.getPlayer().getPlayerData().getStat(Stats.EXTRA_SCRAP)));
			}
		} else {
			modifiedAmount = amount;
		}
		
		for (int i = 0; i < modifiedAmount; i++) {
			new Scrap(state, startPos, score);
		}
	}
}
