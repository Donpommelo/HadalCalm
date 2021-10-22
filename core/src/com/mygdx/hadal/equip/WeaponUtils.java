package com.mygdx.hadal.equip;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.joints.WeldJointDef;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.effects.HadalColor;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.event.Event.eventSyncTypes;
import com.mygdx.hadal.event.Scrap;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.event.utility.Sensor;
import com.mygdx.hadal.schmucks.bodies.HadalEntity;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity.particleSyncType;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.bodies.hitboxes.RangedHitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.server.AlignmentFilter;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.strategies.HitboxStrategy;
import com.mygdx.hadal.strategies.hitbox.*;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.Stats;
import com.mygdx.hadal.utils.b2d.BodyBuilder;
import com.mygdx.hadal.utils.b2d.FixtureBuilder;

import java.util.concurrent.ThreadLocalRandom;

import static com.mygdx.hadal.utils.Constants.PPM;

/**
 * This util contains several shortcuts for hitbox-spawning effects for weapons or other items.
 * Includes create explosion, missiles, homing missiles, grenades and bees.
 * @author Lotticelli Lamhock
 */
public class WeaponUtils {

	private static final float selfDamageReduction = 0.5f;
	private static final float explosionSpriteScaling = 1.5f;
	private static final Sprite boomSprite = Sprite.BOOM;
	private static final Sprite grenadeSprite = Sprite.GRENADE;
	private static final Sprite bombSprite = Sprite.BOMB;
	private static final Sprite sparkSprite = Sprite.SPARKS;
	private static final Sprite torpedoSprite = Sprite.TORPEDO;
	private static final Sprite missileSprite = Sprite.MISSILE_B;
	private static final Sprite beeSprite = Sprite.BEE;

	public static void createExplosion(PlayState state, Vector2 startPos, float size, Schmuck user,
									   float explosionDamage, float explosionKnockback, short filter) {
		
		float newSize = size * (1 + user.getBodyData().getStat(Stats.EXPLOSION_SIZE));

		//this prevents players from damaging allies with explosives in the hub
		short actualFilter = filter;
		if (user.getHitboxfilter() == Constants.PLAYER_HITBOX && state.getMode().isHub()) {
			actualFilter = Constants.PLAYER_HITBOX;
		}

		Hitbox hbox = new Hitbox(state, startPos, new Vector2(newSize, newSize), 0.4f, new Vector2(0, 0),
			actualFilter, true, false, user, boomSprite);
		hbox.setSpriteSize(new Vector2(newSize, newSize).scl(explosionSpriteScaling));
		hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
		hbox.addStrategy(new Static(state, hbox, user.getBodyData()));
		hbox.addStrategy(new ExplosionDefault(state, hbox, user.getBodyData(), explosionDamage, explosionKnockback,
			selfDamageReduction, DamageTypes.EXPLOSIVE));

	}
	
	public static void createGrenade(PlayState state, Vector2 startPos, Vector2 size, Schmuck user, float baseDamage, float knockback, float lifespan,
			Vector2 startVelocity, boolean procEffects, int explosionRadius, float explosionDamage, float explosionKnockback, short filter) {
		
		Hitbox hbox = new RangedHitbox(state, startPos, size, lifespan, startVelocity, filter, false, procEffects, user, grenadeSprite);
		hbox.setGravity(2.5f);
		hbox.setRestitution(0.5f);
		
		hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
		hbox.addStrategy(new AdjustAngle(state, hbox, user.getBodyData()));
		hbox.addStrategy(new DropThroughPassability(state, hbox, user.getBodyData()));	
		hbox.addStrategy(new ContactUnitDie(state, hbox, user.getBodyData()));
		hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), baseDamage, knockback, DamageTypes.EXPLOSIVE, DamageTypes.RANGED));
		hbox.addStrategy(new DieExplode(state, hbox, user.getBodyData(), explosionRadius, explosionDamage, explosionKnockback, (short) 0));
		hbox.addStrategy(new DieSound(state, hbox, user.getBodyData(), SoundEffect.BOMB, 0.4f));
		hbox.addStrategy(new ContactWallSound(state, hbox, user.getBodyData(), SoundEffect.WALL_HIT1, 0.2f));
		hbox.addStrategy(new FlashNearDeath(state, hbox, user.getBodyData(), 1.0f));
	}

	public static void createBomb(PlayState state, Vector2 startPos, Vector2 spriteSize, Vector2 projSize, Schmuck user,
		  float baseDamage, float knockback, float lifespan, Vector2 startVelocity, boolean procEffects, int explosionRadius,
		  float explosionDamage, float explosionKnockback, short filter) {

		Hitbox hbox = new RangedHitbox(state, startPos, projSize, lifespan, startVelocity, filter, false, procEffects, user, bombSprite);
		hbox.setSpriteSize(spriteSize);
		hbox.setGravity(2.5f);
		hbox.setRestitution(0.5f);

		Hitbox sparks = new RangedHitbox(state, startPos, projSize, lifespan, startVelocity, filter, true, false, user, sparkSprite);
		sparks.setSpriteSize(spriteSize);
		sparks.setEffectsHit(false);
		sparks.setEffectsVisual(false);
		sparks.setEffectsMovement(false);

		sparks.addStrategy(new ControllerDefault(state, sparks, user.getBodyData()));
		sparks.addStrategy(new FixedToEntity(state, sparks, user.getBodyData(), hbox, new Vector2(), new Vector2(), false));

		hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
		hbox.addStrategy(new DropThroughPassability(state, hbox, user.getBodyData()));
		hbox.addStrategy(new ContactUnitDie(state, hbox, user.getBodyData()));
		hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), baseDamage, knockback, DamageTypes.EXPLOSIVE, DamageTypes.RANGED));
		hbox.addStrategy(new DieExplode(state, hbox, user.getBodyData(), explosionRadius, explosionDamage, explosionKnockback, (short) 0));
		hbox.addStrategy(new DieSound(state, hbox, user.getBodyData(), SoundEffect.BOMB, 0.4f));
		hbox.addStrategy(new ContactWallSound(state, hbox, user.getBodyData(), SoundEffect.WALL_HIT1, 0.2f));
		hbox.addStrategy(new FlashNearDeath(state, hbox, user.getBodyData(), 1.0f));
	}
	
	public static void createTorpedo(PlayState state, Vector2 startPos, Vector2 size, Schmuck user, float baseDamage, float knockback, float lifespan,
			Vector2 startVelocity, boolean procEffects,	int explosionRadius, float explosionDamage, float explosionKnockback, short filter) {
		
		Hitbox hbox = new RangedHitbox(state, startPos, size, lifespan, startVelocity, filter, true, procEffects, user, torpedoSprite);
		
		hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
		hbox.addStrategy(new AdjustAngle(state, hbox, user.getBodyData()));
		hbox.addStrategy(new ContactUnitDie(state, hbox, user.getBodyData()));
		hbox.addStrategy(new ContactWallDie(state, hbox, user.getBodyData()));
		hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), baseDamage, knockback, DamageTypes.EXPLOSIVE, DamageTypes.RANGED));
		hbox.addStrategy(new DieExplode(state, hbox, user.getBodyData(), explosionRadius, explosionDamage, explosionKnockback, (short) 0));
		hbox.addStrategy(new CreateParticles(state, hbox, user.getBodyData(), Particle.BUBBLE_TRAIL, 0.0f, 1.0f));
		hbox.addStrategy(new DieSound(state, hbox, user.getBodyData(), SoundEffect.EXPLOSION1, 0.5f));
		hbox.addStrategy(new FlashNearDeath(state, hbox, user.getBodyData(), 1.0f));
	}
	
	private static final float torpedoBaseDamage = 3.0f;
	private static final float torpedoBaseKnockback = 3.0f;
	private static final float torpedoExplosionKnockback = 16.0f;
	private static final int torpedoExplosionRadius = 150;
	private static final int torpedoWidth = 60;
	private static final int torpedoHeight = 14;
	private static final float torpedoLifespan = 8.0f;
	private static final int torpedoSpread = 30;
	private static final float torpedoHoming = 100;
	private static final int torpedoHomingRadius = 100;

	public static void createHomingTorpedo(PlayState state, Vector2 startPos, Schmuck user, float damage, int numTorp,
										   Vector2 startVelocity, boolean procEffects, short filter) {
		
		for (int i = 0; i < numTorp; i++) {
			
			Hitbox hbox = new RangedHitbox(state, startPos, new Vector2(torpedoWidth, torpedoHeight), torpedoLifespan,
				startVelocity, filter, true, procEffects, user, missileSprite);

			hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
			hbox.addStrategy(new AdjustAngle(state, hbox, user.getBodyData()));
			hbox.addStrategy(new ContactUnitDie(state, hbox, user.getBodyData()));
			hbox.addStrategy(new ContactWallDie(state, hbox, user.getBodyData()));
			hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), torpedoBaseDamage, torpedoBaseKnockback, DamageTypes.EXPLOSIVE, DamageTypes.RANGED));
			hbox.addStrategy(new DieExplode(state, hbox, user.getBodyData(), torpedoExplosionRadius, damage, torpedoExplosionKnockback, filter));
			hbox.addStrategy(new HomingUnit(state, hbox, user.getBodyData(), torpedoHoming, torpedoHomingRadius));
			hbox.addStrategy(new Spread(state, hbox, user.getBodyData(), torpedoSpread));
			hbox.addStrategy(new DieSound(state, hbox, user.getBodyData(), SoundEffect.EXPLOSION6, 0.25f));
			hbox.addStrategy(new FlashNearDeath(state, hbox, user.getBodyData(), 1.0f));
		}
	}
	
	private static final float beeBaseDamage = 6.0f;
	private static final float beeKnockback = 8.0f;
	private static final int beeWidth = 20;
	private static final int beeHeight = 18;
	private static final int beeDurability = 5;
	private static final float beeLifespan = 5.0f;
	private static final int beeSpread = 25;
	private static final float beeHoming = 90;

	public static void createBees(PlayState state, Vector2 startPos, Schmuck user, int numBees, int homeRadius,
								  Vector2 startVelocity, boolean procEffects, short filter) {

		for (int i = 0; i < numBees; i++) {
			
			Hitbox hbox = new RangedHitbox(state, startPos, new Vector2(beeWidth, beeHeight), beeLifespan,
				startVelocity, filter, false, procEffects, user, beeSprite);
			hbox.setDensity(0.5f);
			hbox.setDurability(beeDurability);
			hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
			hbox.addStrategy(new ContactUnitLoseDurability(state, hbox, user.getBodyData()));
			hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), beeBaseDamage, beeKnockback, DamageTypes.BEES, DamageTypes.RANGED).setRepeatable(true));
			hbox.addStrategy(new HomingUnit(state, hbox, user.getBodyData(), beeHoming, homeRadius).setDisruptable(true));
			hbox.addStrategy(new Spread(state, hbox, user.getBodyData(), beeSpread));
			hbox.addStrategy(new CreateSound(state, hbox, user.getBodyData(), SoundEffect.BEE_BUZZ, 0.5f, true));
		}
	}
	
	private static final int spiritSize = 25;
	private static final float spiritHoming = 120;
	private static final int spiritHomingRadius = 40;
	public static void releaseVengefulSpirits(PlayState state, Vector2 startPos, float spiritLifespan, float spiritDamage,
											  float spiritKnockback, BodyData creator, Particle particle, short filter, boolean attach) {
		
		Hitbox hbox = new RangedHitbox(state, startPos, new Vector2(spiritSize, spiritSize), spiritLifespan,
			new Vector2(), filter, true, true, creator.getSchmuck(), Sprite.NOTHING);
		
		hbox.addStrategy(new ControllerDefault(state, hbox, creator));
		hbox.addStrategy(new ContactUnitDie(state, hbox, creator));
		hbox.addStrategy(new DamageStandard(state, hbox, creator, spiritDamage, spiritKnockback, DamageTypes.MAGIC, DamageTypes.RANGED));

		//attached hboxes will follow the player until they have a target to home in on
		if (attach) {
			hbox.addStrategy(new HomingUnit(state, hbox, creator, spiritHoming, spiritHomingRadius).setFixedUntilHome(true).setTarget(creator.getSchmuck()));
		} else {
			hbox.addStrategy(new HomingUnit(state, hbox, creator, spiritHoming, spiritHomingRadius));
		}
		hbox.addStrategy(new CreateParticles(state, hbox, creator, particle, 0.0f, 1.0f).setParticleColor(HadalColor.RANDOM));
		
		hbox.addStrategy(new DieSound(state, hbox, creator, SoundEffect.DARKNESS1, 0.25f));
	}
	
	public static void createExplodingReticle(PlayState state, Vector2 startPos, Schmuck user, float reticleSize,
											  float reticleLifespan, float explosionDamage, float explosionKnockback, int explosionRadius) {
		Hitbox hbox = new RangedHitbox(state, startPos, new Vector2(reticleSize, reticleSize), reticleLifespan,
			new Vector2(), user.getHitboxfilter(), true, false, user, Sprite.CROSSHAIR);
		hbox.setPassability((short) (Constants.BIT_PROJECTILE | Constants.BIT_WALL | Constants.BIT_PLAYER | Constants.BIT_ENEMY));
		
		hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
		hbox.addStrategy(new CreateParticles(state, hbox, user.getBodyData(), Particle.EVENT_HOLO, 0.0f, 1.0f).setParticleSize(40.0f).setParticleColor(
			HadalColor.HOT_PINK));
		hbox.addStrategy(new DieExplode(state, hbox, user.getBodyData(), explosionRadius, explosionDamage, explosionKnockback, user.getHitboxfilter()));
		hbox.addStrategy(new DieSound(state, hbox, user.getBodyData(), SoundEffect.EXPLOSION6, 0.25f));
		hbox.addStrategy(new Static(state, hbox, user.getBodyData()));
	}
	
	private static final float primeDelay = 1.0f;
	private static final float projDampen = 1.0f;
	private static final float footballThreshold = 200.0f;
	private static final float footballDepreciation = 50.0f;
	public static Hitbox createNauticalMine(PlayState state, Vector2 startPos, Schmuck user,
											Vector2 startVelocity, float mineSize, float mineLifespan, float explosionDamage,
											float explosionKnockback, int explosionRadius, float pushMultiplier, boolean event) {
		Hitbox hbox = new RangedHitbox(state, startPos, new Vector2(mineSize, mineSize), mineLifespan, startVelocity,
			(short) 0, false, false, user, Sprite.NAVAL_MINE);
		hbox.setRestitution(0.5f);

		hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
		if (event) {
			hbox.addStrategy(new ContactGoalScore(state, hbox, user.getBodyData()));
			hbox.addStrategy(new DamageThresholdDie(state, hbox, user.getBodyData(), footballThreshold, footballDepreciation));
		} else {
			hbox.addStrategy(new ContactUnitDie(state, hbox, user.getBodyData()).setDelay(primeDelay));
		}
		hbox.addStrategy(new AdjustAngle(state, hbox, user.getBodyData()));
		hbox.addStrategy(new Pushable(state, hbox, user.getBodyData(), pushMultiplier));
		hbox.addStrategy(new DieExplode(state, hbox, user.getBodyData(), explosionRadius, explosionDamage, explosionKnockback, (short) 0));
		hbox.addStrategy(new DieSound(state, hbox, user.getBodyData(), SoundEffect.EXPLOSION_FUN, 0.4f));
		hbox.addStrategy(new FlashNearDeath(state, hbox, user.getBodyData(), 1.0f));
		hbox.addStrategy(new HitboxStrategy(state, hbox, user.getBodyData()) {
			
			@Override
			public void create() {
				super.create();
				hbox.getBody().setLinearDamping(projDampen);
			}
		});

		return hbox;
	}

	public static void createProximityMine(PlayState state, Vector2 startPos, Schmuck user, float startVelocity, Vector2 mineSize,
										   float primeTime, float mineLifespan, float explosionDamage, float explosionKnockback, int explosionRadius) {
		Hitbox hbox = new RangedHitbox(state, startPos, mineSize, primeTime,  new Vector2(0, -startVelocity),
			user.getHitboxfilter(), false, false, user, Sprite.LAND_MINE);
		hbox.setPassability((short) (Constants.BIT_WALL | Constants.BIT_DROPTHROUGHWALL));
		hbox.makeUnreflectable();
		hbox.setGravity(3.0f);

		hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
		hbox.addStrategy(new DropThroughPassability(state, hbox, user.getBodyData()));
		hbox.addStrategy(new DieParticles(state, hbox, user.getBodyData(), Particle.SMOKE).setParticleSize(150));
		hbox.addStrategy(new HitboxStrategy(state, hbox, user.getBodyData()) {

			private HadalEntity floor;
			private boolean planted, set;
			private float primeCount;
			@Override
			public void onHit(HadalData fixB) {
				if (fixB != null) {
					floor = fixB.getEntity();
					if (floor != null) {
						if (floor.getBody() != null) {
							planted = true;
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

					SoundEffect.SLAP.playUniversal(state, hbox.getPixelPosition(), 0.6f, false);
					set = true;
				}
				if (set) {
					primeCount += delta;
					if (primeCount >= primeTime) {
						SoundEffect.MAGIC27_EVIL.playUniversal(state, hbox.getPixelPosition(), 1.0f, false);
						hbox.die();
					}
				}
			}

			@Override
			public void die() {
				Hitbox mine = new RangedHitbox(state, hbox.getPixelPosition(), mineSize, mineLifespan,  new Vector2(),
					(short) 0, true, false, user, Sprite.NOTHING);
				mine.makeUnreflectable();

				mine.addStrategy(new ControllerDefault(state, mine, user.getBodyData()));
				mine.addStrategy(new ContactUnitDie(state, mine, user.getBodyData()));
				mine.addStrategy(new DieExplode(state, mine, user.getBodyData(), explosionRadius, explosionDamage, explosionKnockback, (short) 0));
				mine.addStrategy(new DieSound(state, mine, user.getBodyData(), SoundEffect.EXPLOSION6, 0.6f));
				mine.addStrategy(new HitboxStrategy(state, mine, user.getBodyData()) {

					@Override
					public void create() {
						if (floor != null) {
							if (floor.getBody() != null && hbox.getBody() != null) {
								WeldJointDef joint = new WeldJointDef();
								joint.bodyA = floor.getBody();
								joint.bodyB = hbox.getBody();
								joint.localAnchorA.set(new Vector2(hbox.getPosition()).sub(floor.getPosition()));
								joint.localAnchorB.set(0, 0);
								state.getWorld().createJoint(joint);
							}
						}
					}
				});
			}
		});
	}

	private static final Sprite[] projSprites = {Sprite.METEOR_A, Sprite.METEOR_B, Sprite.METEOR_C, Sprite.METEOR_D, Sprite.METEOR_E, Sprite.METEOR_F};
	private static final Vector2 meteorSize = new Vector2(75, 75);
	private static final float meteorSpeed = 50.0f;
	private static final float range = 1500.0f;
	private static final float lifespan = 5.0f;
	
	public static void createMeteors(PlayState state, Vector2 startPos, Schmuck user, float meteorDuration, float meteorInterval,
									 float spread, float baseDamage, float knockback) {
		Hitbox hbox = new RangedHitbox(state, startPos, new Vector2(1, 1), meteorDuration, new Vector2(),
			(short) 0, false, false, user, Sprite.NOTHING);
		hbox.makeUnreflectable();
		hbox.setSyncDefault(false);
		
		hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
		hbox.addStrategy(new Static(state, hbox, user.getBodyData()));
		hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
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
					endPt.set(originPt).add(0, -range);
					shortestFraction = 1.0f;
					
					if (originPt.x != endPt.x || originPt.y != endPt.y) {

						state.getWorld().rayCast((fixture, point, normal, fraction) -> {
							if (fixture.getFilterData().categoryBits == Constants.BIT_WALL && fraction < shortestFraction) {
								shortestFraction = fraction;
								return fraction;
						}
						return -1.0f;
						}, originPt, endPt);
					}
					
					endPt.set(originPt).add(0, -range * shortestFraction).scl(PPM);
					originPt.set(endPt).add(0, range);
					
					int randomIndex = MathUtils.random(projSprites.length - 1);
					Sprite projSprite = projSprites[randomIndex];
					
					
					Hitbox hbox = new Hitbox(state, new Vector2(originPt), meteorSize, lifespan, new Vector2(0, -meteorSpeed), user.getHitboxfilter(), true, false, user, projSprite);
					hbox.setPassability((short) (Constants.BIT_PROJECTILE | Constants.BIT_WALL | Constants.BIT_PLAYER | Constants.BIT_ENEMY));

					hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
					hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), baseDamage, knockback, DamageTypes.FIRE, DamageTypes.MAGIC));
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

	private static final Sprite[] vineSprites = {Sprite.VINE_A, Sprite.VINE_C, Sprite.VINE_D};
	public static void createVine(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelo,
								  int vineNum, float lifespan, float vineDamage, float vineKB,
								  int spreadMin, int spreadMax, int bendLength, int bendSpread,
								  Vector2 vineInvisSize, Vector2 vineSize, Vector2 vineSpriteSize, int splitNum) {

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
			private final Vector2 entityLocation = new Vector2();
			private int vineCount, vineCountTotal, nextBend;
			private boolean bendRight;
			private float displacement;
			private final Vector2 angle = new Vector2();

			@Override
			public void controller(float delta) {
				entityLocation.set(hbox.getPixelPosition());

				displacement += lastPosition.dst(entityLocation);
				lastPosition.set(entityLocation);

				//after moving distance equal to a vine, the hbox spawns a vine with random sprite
				if (displacement > vineSize.x) {
					displacement = 0.0f;

					int randomIndex = MathUtils.random(vineSprites.length - 1);
					Sprite projSprite = vineSprites[randomIndex];

					RangedHitbox vine = new RangedHitbox(state, hbox.getPixelPosition(), vineSize, lifespan, new Vector2(),
						user.getHitboxfilter(), true, false, creator.getSchmuck(),
						vineCountTotal == vineNum ? Sprite.VINE_B : projSprite) {

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

					vine.addStrategy(new ControllerDefault(state, vine, user.getBodyData()));
					vine.addStrategy(new ContactUnitSound(state, vine, user.getBodyData(), SoundEffect.STAB, 0.6f, true));
					vine.addStrategy(new DamageStandard(state, vine, user.getBodyData(), vineDamage, vineKB, DamageTypes.RANGED).setStaticKnockback(true));
					vine.addStrategy(new CreateParticles(state, vine, user.getBodyData(), Particle.DANGER_RED, 0.0f, 1.0f).setParticleSize(90.0f));
					vine.addStrategy(new DieParticles(state, vine, user.getBodyData(), Particle.PLANT_FRAG));
					vine.addStrategy(new Static(state, vine, user.getBodyData()));

					vineCount++;
					vineCountTotal++;
					if (vineCount >= nextBend) {

						//hbox's velocity changes randomly to make vine wobble
						hbox.setLinearVelocity(hbox.getLinearVelocity().rotateDeg((bendRight ? -1 : 1) * ThreadLocalRandom
							.current().nextInt(spreadMin, spreadMax)));
						bendRight = !bendRight;
						vineCount = 0;
						nextBend = bendLength + (ThreadLocalRandom.current().nextInt(-bendSpread, bendSpread + 1));
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
					float
						newDegrees =
						hbox.getLinearVelocity().angleDeg() +
							(ThreadLocalRandom.current().nextInt(spreadMin, spreadMax));
					angle.set(hbox.getLinearVelocity()).setAngleDeg(newDegrees);
					WeaponUtils.createVine(state, user, hbox.getPixelPosition(), angle, vineNum, lifespan,
						vineDamage, vineKB, spreadMin, spreadMax, 2, 1,
						vineInvisSize, vineSize, vineSpriteSize, splitNum - 1);

					newDegrees =
						hbox.getLinearVelocity().angleDeg() -
							(ThreadLocalRandom.current().nextInt(spreadMin, spreadMax));
					angle.set(hbox.getLinearVelocity()).setAngleDeg(newDegrees);
					WeaponUtils.createVine(state, user, hbox.getPixelPosition(), angle, vineNum, lifespan,
						vineDamage, vineKB, spreadMin, spreadMax, 2, 1,
						vineInvisSize, vineSize, vineSpriteSize, splitNum - 1);
				}
			}
		});

	}

	private static final Vector2 pingSize = new Vector2(60, 54);
	private static final Vector2 pingArrowSize = new Vector2(60, 33);
	private static final float pingLifespan = 2.0f;
	public static void ping(PlayState state, Vector2 startPos, Schmuck user, short filter) {
		SoundEffect.PING.playUniversal(state, startPos, 0.6f, false);

		Hitbox hbox = new RangedHitbox(state, new Vector2(startPos).add(0, 35), pingSize, pingLifespan, new Vector2(), filter, true, false, user, Sprite.NOTIFICATIONS_ALERT);

		hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
		hbox.addStrategy(new Static(state, hbox, user.getBodyData()));

		Hitbox hboxPing = new RangedHitbox(state, new Vector2(startPos).add(0, -10), pingArrowSize, pingLifespan, new Vector2(), filter, true, false, user, Sprite.NOTIFICATIONS_ALERT_PING);
		hboxPing.setSpriteSize(pingArrowSize);

		hboxPing.addStrategy(new ControllerDefault(state, hboxPing, user.getBodyData()));
		hboxPing.addStrategy(new Static(state, hboxPing, user.getBodyData()));
	}

	private static final Vector2 emoteSize = new Vector2(64, 64);
	private static final float emoteLifespan = 1.9f;
	private static final float emoteLifespanLong = 6.0f;
	private static final int emoteExplodeRadius = 150;
	private static final float emoteExplodeDamage = 75.0f;
	private static final float emoteExplodeback = 20;

	public static void emote(PlayState state, Schmuck user, Sprite emote) {

		boolean special = user.getBodyData().getStat(Stats.PING_DAMAGE) != 0.0f;

		Hitbox hbox = new RangedHitbox(state, new Vector2(user.getPixelPosition()).add(0, user.getSize().y / 2 + 50), emoteSize,
			special ? emoteLifespanLong : emoteLifespan, new Vector2(), (short) 0, !special, special, user, emote);

		hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
		hbox.addStrategy(new HitboxStrategy(state, hbox, user.getBodyData()) {

			private float controllerCount;
			private final Vector2 entityLocation = new Vector2();
			@Override
			public void controller(float delta) {
				controllerCount += delta;

				if (controllerCount <= emoteLifespan) {
					entityLocation.set(user.getPosition()).add(0, (user.getSize().y / 2 + 50) / PPM);
					hbox.setTransform(entityLocation, hbox.getAngle());
					hbox.setLinearVelocity(user.getLinearVelocity());
				}
			}
		});

		//with the Finger equipped, emotes detach and explode
		if (special) {
			hbox.setRestitution(0.5f);
			hbox.addStrategy(new Pushable(state, hbox, user.getBodyData(), 1.0f));
			hbox.addStrategy(new ContactUnitDie(state, hbox, user.getBodyData()).setDelay(emoteLifespan + 1.0f));
			hbox.addStrategy(new DieExplode(state, hbox, user.getBodyData(), emoteExplodeRadius, emoteExplodeDamage, emoteExplodeback, (short) 0));
			hbox.addStrategy(new DieSound(state, hbox, user.getBodyData(), SoundEffect.EXPLOSION_FUN, 0.4f));
			hbox.addStrategy(new FlashNearDeath(state, hbox, user.getBodyData(), 1.0f));
			hbox.addStrategy(new HitboxStrategy(state, hbox, user.getBodyData()) {

				@Override
				public void create() {
					super.create();
					hbox.getBody().setLinearDamping(projDampen);
				}
			});
		}
	}

	/**
	 * This method returns a player's "color" corresponding to their team color or their character with no team.
	 * This is used to color code player name as well as for streak particle coloring
	 */
	public static Vector3 getPlayerColor(Player player) {

		//return empty vector if player's data has not been created yet.
		if (player.getPlayerData() != null) {
			Loadout loadout = player.getPlayerData().getLoadout();
			if (loadout.team.equals(AlignmentFilter.NONE)) {
				return loadout.character.getColor1();
			} else if (loadout.team.getColor1RGB().isZero()) {
				return loadout.character.getColor1();
			} else {
				return loadout.team.getColor1RGB();
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
		rgb.set(color.getR(), color.getG(), color.getB());
		String hex = "#" + Integer.toHexString(Color.rgb888(rgb.x, rgb.y, rgb.z));
		return "[" + hex + "]" + name + "[]";
	}

	public static final int pickupSize = 64;
	public static void createPickup(PlayState state, Vector2 startPos, final pickupTypes type, final float power) {

		Event pickup = new Sensor(state, startPos, new Vector2(pickupSize, pickupSize), true, false, false, false, 1.0f, true) {
			
			@Override
			public void create() {
				
				this.eventData = new EventData(this) {
					
					@Override
					public void onTouch(HadalData fixB) {
						super.onTouch(fixB);
						
						if (isAlive() && fixB instanceof PlayerBodyData player) {
							switch(type) {
							case AMMO:
								
								SoundEffect.LOCKANDLOAD.playUniversal(state, player.getPlayer().getPixelPosition(), 0.3f, false);
								
								player.getCurrentTool().gainAmmo(power);
								new ParticleEntity(state, player.getSchmuck(), Particle.PICKUP_ENERGY, 0.0f, 5.0f, true, particleSyncType.CREATESYNC);
								event.queueDeletion();
								break;
							case FUEL:
								if (player.getCurrentFuel() < player.getStat(Stats.MAX_FUEL)) {
									
									SoundEffect.MAGIC2_FUEL.playUniversal(state, player.getPlayer().getPixelPosition(), 0.3f, false);

									player.fuelGain(power);
									new ParticleEntity(state, player.getSchmuck(), Particle.PICKUP_ENERGY, 3.0f, 5.0f, true, particleSyncType.CREATESYNC);
									event.queueDeletion();
								}
								break;
							case HEALTH:
								if (player.getCurrentHp() < player.getStat(Stats.MAX_HP)) {
									
									SoundEffect.MAGIC21_HEAL.playUniversal(state, player.getPlayer().getPixelPosition(), 0.3f, false);
									
									player.regainHp(power, player, true, DamageTypes.MEDPAK);
									new ParticleEntity(state, player.getSchmuck(), Particle.PICKUP_HEALTH, 3.0f, 5.0f, true, particleSyncType.CREATESYNC);
									event.queueDeletion();
								}
								break;
							default:
								break;
							}
						}
					}
				};
				this.body = BodyBuilder.createBox(world, startPos, size, gravity, 0, 0, false, false, Constants.BIT_SENSOR, Constants.BIT_PLAYER, (short) 0, true, eventData);
				
				FixtureBuilder.createFixtureDef(body, new Vector2(), size, false, 0, 0, 0.0f, 1.0f, Constants.BIT_SENSOR, Constants.BIT_WALL, (short) 0);
			}
		};
		
		new ParticleEntity(state, pickup, Particle.EVENT_HOLO, 1.0f, 0.0f, true, particleSyncType.CREATESYNC);
		pickup.setScaleAlign("CENTER_BOTTOM");
		pickup.setSyncType(eventSyncTypes.ILLUSION);
		pickup.setSynced(true);
		pickup.setScale(0.25f);
		
		switch(type) {
		case AMMO:
		case FUEL:
			pickup.setEventSprite(Sprite.FUEL);
			break;
		case HEALTH:
			pickup.setEventSprite(Sprite.MEDPAK);
			break;
		default:
			break;
		}
	}
	
	/**
	 * This spawns some amount of scrap events as currency for the player
	 * @param statCheck: do we take into account the player's bonus scrap drop?
	 * @param score: does picking up the screp increment the player's score?
	 */
	public static void spawnScrap(PlayState state, int amount, Vector2 startPos, boolean statCheck, boolean score) {
		
		int modifiedAmount;
		
		if (statCheck && state.getPlayer().getPlayerData() != null) {
			if (state.getPlayer().getPlayerData().getStat(Stats.EXTRA_SCRAP) * amount < 1.0f && state.getPlayer().getPlayerData().getStat(Stats.EXTRA_SCRAP) > 0) {
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
	
	public enum pickupTypes {
		HEALTH,
		FUEL,
		AMMO
	}
}
