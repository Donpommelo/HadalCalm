package com.mygdx.hadal.equip;

import com.badlogic.gdx.graphics.Color;
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
import com.mygdx.hadal.managers.GameStateManager;
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
	private static final Sprite torpedoSprite = Sprite.TORPEDO;
	private static final Sprite missileSprite = Sprite.MISSILE_B;
	private static final Sprite beeSprite = Sprite.BEE;

	public static Hitbox createExplosion(PlayState state, Vector2 startPos, float size, Schmuck user, float explosionDamage, float explosionKnockback, short filter) {
		
		float newSize = size * (1 + user.getBodyData().getStat(Stats.EXPLOSION_SIZE));
		
		Hitbox hbox = new Hitbox(state, startPos, new Vector2(newSize, newSize), 0.4f, new Vector2(0, 0), filter, true, false, user, boomSprite);
		hbox.setSpriteSize(new Vector2(newSize, newSize).scl(explosionSpriteScaling));
		hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
		hbox.addStrategy(new Static(state, hbox, user.getBodyData()));
		hbox.addStrategy(new ExplosionDefault(state, hbox, user.getBodyData(), explosionDamage, explosionKnockback, selfDamageReduction, DamageTypes.EXPLOSIVE));
		
		return hbox;
	}
	
	public static void createGrenade(PlayState state, Vector2 startPos, Vector2 size, Schmuck user, float baseDamage, float knockback, float lifespan,
			Vector2 startVelocity, boolean procEffects, int explosionRadius, float explosionDamage, float explosionKnockback, short filter) {
		
		Hitbox hbox = new RangedHitbox(state, startPos, size, lifespan, startVelocity, filter, false, procEffects, user, grenadeSprite);
		hbox.setGravity(2.5f);
		hbox.setRestitution(0.5f);
		
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
	
	public static void createHomingTorpedo(PlayState state, Vector2 startPos, Schmuck user, float damage, int numTorp, Vector2 startVelocity, boolean procEffects, short filter) {
		
		for (int i = 0; i < numTorp; i++) {
			
			Hitbox hbox = new RangedHitbox(state, startPos, new Vector2(torpedoWidth, torpedoHeight), torpedoLifespan, startVelocity, filter, true, procEffects, user, missileSprite);

			hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
			hbox.addStrategy(new AdjustAngle(state, hbox, user.getBodyData()));
			hbox.addStrategy(new ContactUnitDie(state, hbox, user.getBodyData()));
			hbox.addStrategy(new ContactWallDie(state, hbox, user.getBodyData()));
			hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), torpedoBaseDamage, torpedoBaseKnockback, DamageTypes.EXPLOSIVE, DamageTypes.RANGED));
			hbox.addStrategy(new DieExplode(state, hbox, user.getBodyData(), torpedoExplosionRadius, damage, torpedoExplosionKnockback, filter));
			hbox.addStrategy(new HomingUnit(state, hbox, user.getBodyData(), torpedoHoming));
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
	private static final int beeSpread = 60;
	private static final float beeHoming = 100;
	
	public static Hitbox createBees(PlayState state, Vector2 startPos, Schmuck user, int numBees, Vector2 startVelocity, boolean procEffects, short filter) {

		for (int i = 0; i < numBees; i++) {
			
			Hitbox hbox = new RangedHitbox(state, startPos, new Vector2(beeWidth, beeHeight), beeLifespan, startVelocity, filter, false, procEffects, user, beeSprite);
			hbox.setDensity(0.5f);
			hbox.setDurability(beeDurability);
			hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
			hbox.addStrategy(new ContactUnitLoseDurability(state, hbox, user.getBodyData()));
			hbox.addStrategy(new DamageStandardRepeatable(state, hbox, user.getBodyData(), beeBaseDamage, beeKnockback, DamageTypes.BEES, DamageTypes.RANGED));	
			hbox.addStrategy(new HomingUnit(state, hbox, user.getBodyData(), beeHoming).setDisruptable(true));
			hbox.addStrategy(new Spread(state, hbox, user.getBodyData(), beeSpread));
			hbox.addStrategy(new CreateSound(state, hbox, user.getBodyData(), SoundEffect.BEE_BUZZ, 0.5f, true));
		}
		
		return null;
	}
	
	private static final int spiritSize = 25;
	private static final float spiritHoming = 80;
	public static void releaseVengefulSpirits(PlayState state, Vector2 startPos, float spiritLifespan, float spiritDamage, float spiritKnockback, BodyData creator, Particle particle, short filter) {		
		
		Hitbox hbox = new RangedHitbox(state, startPos, new Vector2(spiritSize, spiritSize), spiritLifespan, new Vector2(), filter, true, true, creator.getSchmuck(), Sprite.NOTHING);
		
		hbox.addStrategy(new ControllerDefault(state, hbox, creator));
		hbox.addStrategy(new ContactUnitDie(state, hbox, creator));
		hbox.addStrategy(new DamageStandard(state, hbox, creator, spiritDamage, spiritKnockback, DamageTypes.MAGIC, DamageTypes.RANGED));
		hbox.addStrategy(new HomingUnit(state, hbox, creator, spiritHoming));
		hbox.addStrategy(new CreateParticles(state, hbox, creator, particle, 0.0f, 1.0f).setParticleColor(HadalColor.RANDOM));
		
		hbox.addStrategy(new DieSound(state, hbox, creator, SoundEffect.DARKNESS1, 0.25f));
	}
	
	public static void createExplodingReticle(PlayState state, Vector2 startPos, Schmuck user, float reticleSize, float reticleLifespan, float explosionDamage, float explosionKnockback, int explosionRadius) {
		Hitbox hbox = new RangedHitbox(state, startPos, new Vector2(reticleSize, reticleSize), reticleLifespan, new Vector2(), user.getHitboxfilter(), true, false, user, Sprite.CROSSHAIR);
		hbox.setPassability((short) (Constants.BIT_PROJECTILE | Constants.BIT_WALL | Constants.BIT_PLAYER | Constants.BIT_ENEMY));
		
		hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
		hbox.addStrategy(new CreateParticles(state, hbox, user.getBodyData(), Particle.EVENT_HOLO, 0.0f, 1.0f).setParticleSize(40.0f).setParticleColor(
			HadalColor.HOT_PINK));
		hbox.addStrategy(new DieExplode(state, hbox, user.getBodyData(), explosionRadius, explosionDamage, explosionKnockback, (short) 0));
		hbox.addStrategy(new DieSound(state, hbox, user.getBodyData(), SoundEffect.EXPLOSION6, 0.25f));
		hbox.addStrategy(new Static(state, hbox, user.getBodyData()));
	}
	
	private static final float primeDelay = 1.0f;
	private static final float projDampen = 1.0f;
	public static void createNauticalMine(PlayState state, Vector2 startPos, Schmuck user, Vector2 startVelocity, float mineSize, float mineLifespan, float explosionDamage, float explosionKnockback, int explosionRadius) {
		Hitbox hbox = new RangedHitbox(state, startPos, new Vector2(mineSize, mineSize), mineLifespan, startVelocity, (short) 0, false, false, user, Sprite.NAVAL_MINE);
		hbox.setRestitution(0.5f);
		
		hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
		hbox.addStrategy(new AdjustAngle(state, hbox, user.getBodyData()));
		hbox.addStrategy(new Pushable(state, hbox, user.getBodyData()));
		hbox.addStrategy(new ContactUnitDie(state, hbox, user.getBodyData()).setDelay(primeDelay));
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
				if (planted) {
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
							if (floor.getBody() != null) {
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
	
	public static void createMeteors(PlayState state, Vector2 startPos, Schmuck user, float meteorDuration, float meteorInterval, float spread, float baseDamage, float knockback) {
		Hitbox hbox = new RangedHitbox(state, startPos, new Vector2(1, 1), meteorDuration, new Vector2(), (short) 0, false, false, user, Sprite.NOTHING);
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
					
					originPt.set(startPos).add((GameStateManager.generator.nextFloat() -  0.5f) * spread, 0);
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
					
					int randomIndex = GameStateManager.generator.nextInt(projSprites.length);
					Sprite projSprite = projSprites[randomIndex];
					
					
					Hitbox hbox = new Hitbox(state, new Vector2(originPt), meteorSize, lifespan, new Vector2(0, -meteorSpeed), user.getHitboxfilter(), true, false, user, projSprite);
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
	
	private static final Vector2 pingSize = new Vector2(60, 54);
	private static final Vector2 pingArrowSize = new Vector2(60, 33);
	private static final float pingLifespan = 2.0f;
	private static final float pingKnockback = 10.0f;
	public static void ping(PlayState state, Vector2 startPos, Schmuck user, short filter) {
		SoundEffect.PING.playUniversal(state, startPos, 0.6f, false);

		Hitbox hbox = new RangedHitbox(state, new Vector2(startPos).add(0, 35), pingSize, pingLifespan, new Vector2(), filter, true, false, user, Sprite.NOTIFICATIONS_ALERT);

		hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
		hbox.addStrategy(new Static(state, hbox, user.getBodyData()));
		
		if (user.getBodyData().getStat(Stats.PING_DAMAGE) != 0.0f) {
			hbox.addStrategy(new DamageStatic(state, hbox, user.getBodyData(), user.getBodyData().getStat(Stats.PING_DAMAGE), pingKnockback));
		}
		
		Hitbox hboxPing = new RangedHitbox(state, new Vector2(startPos).add(0, -10), pingArrowSize, pingLifespan, new Vector2(), filter, true, false, user, Sprite.NOTIFICATIONS_ALERT_PING);
		hboxPing.setSpriteSize(pingArrowSize);

		hboxPing.addStrategy(new ControllerDefault(state, hboxPing, user.getBodyData()));
		hboxPing.addStrategy(new Static(state, hboxPing, user.getBodyData()));
	}

	private static final Vector2 emoteSize = new Vector2(64, 64);
	private static final float emoteLifespan = 1.9f;
	private static final float emoteLifespanLong = 6.0f;
	private static final int emoteExplodeRadius = 150;
	private static final float emoteExplodeDamage = 40.0f;
	private static final float emoteExplodeback = 20;

	public static void emote(PlayState state, Schmuck user, Sprite emote) {

		boolean special = user.getBodyData().getStat(Stats.PING_DAMAGE) != 0.0f;

		Hitbox hbox = new RangedHitbox(state, new Vector2(user.getPixelPosition()).add(0, 88.0f), emoteSize,
			special ? emoteLifespanLong : emoteLifespan, new Vector2(), (short) 0, !special, special, user, emote);

		hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));

		hbox.addStrategy(new HitboxStrategy(state, hbox, user.getBodyData()) {

			private float controllerCount;
			private final Vector2 entityLocation = new Vector2();
			@Override
			public void controller(float delta) {
				controllerCount += delta;

				if (controllerCount <= emoteLifespan) {
					entityLocation.set(user.getPosition()).add(0, 88.f / PPM);
					hbox.setTransform(entityLocation, hbox.getAngle());
					hbox.setLinearVelocity(user.getLinearVelocity());
				}
			}
		});

		if (special) {
			hbox.setRestitution(0.5f);
			hbox.addStrategy(new Pushable(state, hbox, user.getBodyData()));
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

	public static Vector3 getPlayerColor(Player player) {
		Loadout loadout = player.getPlayerData().getLoadout();
		if (loadout.team.equals(AlignmentFilter.NONE)) {
			return loadout.character.getColor1();
		} else if (loadout.team.getColor1().isZero()) {
			return loadout.character.getColor1();
		} else {
			return loadout.team.getColor1();
		}
	}

	private static final Vector3 rgb = new Vector3();
	public static String getPlayerColorName(Schmuck schmuck, int maxNameLen) {
		if (schmuck instanceof Player) {
			Player player = (Player) schmuck;
			String displayedName = player.getName();

			if (displayedName.length() > maxNameLen) {
				displayedName = displayedName.substring(0, maxNameLen).concat("...");
			}

			rgb.set(getPlayerColor(player));
			String hex = "#" + Integer.toHexString(Color.rgb888(rgb.x, rgb.y, rgb.z));
			return "[" + hex + "]" + displayedName + "[]";
		} else {
			return schmuck.getName();
		}
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
						
						if (isAlive() && fixB instanceof PlayerBodyData) {
							
							
							PlayerBodyData player = ((PlayerBodyData)fixB);
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
	 */
	public static void spawnScrap(PlayState state, int amount, Vector2 startPos, boolean statCheck) {
		
		int modifiedAmount;
		
		if (statCheck) {
			if (state.getPlayer().getPlayerData().getStat(Stats.EXTRA_SCRAP) * amount < 1.0f && state.getPlayer().getPlayerData().getStat(Stats.EXTRA_SCRAP) > 0) {
				modifiedAmount = amount + 1;
			} else {
				modifiedAmount = (int) (amount * (1 + state.getPlayer().getPlayerData().getStat(Stats.EXTRA_SCRAP)));
			}
		} else {
			modifiedAmount = amount;
		}
		
		for (int i = 0; i < modifiedAmount; i++) {
			new Scrap(state, startPos);
		}
	}
	
	public enum pickupTypes {
		HEALTH,
		FUEL,
		AMMO
	}
}
