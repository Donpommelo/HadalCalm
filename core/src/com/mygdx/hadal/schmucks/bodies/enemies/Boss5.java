package com.mygdx.hadal.schmucks.bodies.enemies;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.effects.HadalColor;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.EnemyUtils;
import com.mygdx.hadal.equip.WeaponUtils;
import com.mygdx.hadal.event.SpawnerSchmuck;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity;
import com.mygdx.hadal.schmucks.bodies.SoundEntity;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.bodies.hitboxes.RangedHitbox;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.statuses.DeathParticles;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.strategies.HitboxStrategy;
import com.mygdx.hadal.strategies.hitbox.*;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.Stats;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

import static com.mygdx.hadal.utils.Constants.PPM;

/**
 * This is a boss in the game
 */
public class Boss5 extends EnemyFloating {

    private static final float aiAttackCd = 2.1f;
    private static final float aiAttackCd2 = 1.6f;

    private static final int scrapDrop = 15;

	private static final int width = 200;
	private static final int height = 200;

	private static final int hbWidth = 200;
	private static final int hbHeight = 200;

	private static final float scale = 1.0f;

	private static final int hp = 8000;

	private int phase = 1;
	private static final float phaseThreshold2 = 0.0f;

	private final Animation<TextureRegion> coreSprite, bodySprite;
	private final TextureRegion crownSprite;
	private static final float crownWidth = 89;
	private static final float crownHeight = 84;
	private static final float crownOffsetX = 0.0f;
	private static final float crownOffsetY= 110.0f;

	public Boss5(PlayState state, Vector2 startPos, short filter, SpawnerSchmuck spawner) {
		super(state, startPos, new Vector2(width, height).scl(scale), new Vector2(hbWidth, hbHeight).scl(scale), Sprite.NOTHING, EnemyType.BOSS5, filter, hp, aiAttackCd, scrapDrop, spawner);
		this.coreSprite = new Animation<>(PlayState.spriteAnimationSpeedFast, Sprite.NEPTUNE_KING_CORE.getFrames());
		this.bodySprite = new Animation<>(PlayState.spriteAnimationSpeedFast, Sprite.NEPTUNE_KING_BODY.getFrames());
		this.crownSprite = Sprite.NEPTUNE_KING_CROWN.getFrame();

		new ParticleEntity(state, this, Particle.TYRRAZZA_TRAIL, 1.0f, 0.0f, true, ParticleEntity.particleSyncType.TICKSYNC).setScale(2.0f);
	}

	@Override
	public void create() {
		super.create();
		getBodyData().addStatus(new StatChangeStatus(state, Stats.KNOCKBACK_RES, 1.0f, getBodyData()));
		getBodyData().addStatus(new DeathParticles(state, getBodyData(), Particle.DIATOM_IMPACT_LARGE, 5.0f));

	}

	private final Vector2 entityLocation = new Vector2();
	@Override
	public void render(SpriteBatch batch) {
		entityLocation.set(getPixelPosition());
		batch.draw(coreSprite.getKeyFrame(animationTime, true),
			entityLocation.x - size.x / 2,entityLocation.y - size.y / 2,
			size.x / 2, size.y / 2,
			size.x, size.y, 1, 1, 0);

		batch.draw(bodySprite.getKeyFrame(animationTime, true),
			entityLocation.x - size.x / 2,entityLocation.y - size.y / 2,
			size.x / 2, size.y / 2,
			size.x, size.y, 1, 1, 0);

		batch.draw(crownSprite,
			entityLocation.x - crownWidth / 2 + crownOffsetX,entityLocation.y - crownHeight / 2 + crownOffsetY,
			crownWidth / 2, crownHeight / 2,
			crownWidth, crownHeight, 1, 1, 0);
	}

	@Override
	public void multiplayerScaling(int numPlayers) {
		getBodyData().addStatus(new StatChangeStatus(state, Stats.MAX_HP, 1400 * numPlayers, getBodyData()));
	}
	
	private int attackNum;
	@Override
	public void attackInitiate() {
		attackNum++;
		if (phase == 1) {
			phase1Attack();
		}
	}
	
	private static final int phase1NumAttacks = 4;

	//these lists are used to make the boss perform all attacks in its pool before repeating any
	private final ArrayList<Integer> attacks1 = new ArrayList<>();
	private final ArrayList<Integer> attacks2 = new ArrayList<>();
	private void phase1Attack() {
		if (attacks1.isEmpty()) {
			for (int i = 0; i < phase1NumAttacks; i++) {
				attacks1.add(i);
			}
		}
		if (attacks2.isEmpty()) {
			for (int i = 0; i < phase1NumAttacks; i++) {
				attacks2.add(i);
			}
		}

		if (attackNum % 2 == 0) {
			int nextAttack = attacks1.remove(MathUtils.random(attacks1.size() - 1));
			switch (nextAttack) {
				case 0 -> orbitalCharge();
				case 1 -> vineLash();
				case 2 -> scytheAttack();
				case 3 -> spreadingShadow();
			}

		} else {
			int nextAttack = attacks2.remove(MathUtils.random(attacks2.size() - 1));
			switch (nextAttack) {
				case 0 -> tripleRadialBurst();
				case 1 -> sporeBurst();
				case 2 -> seedBomber();
				case 3 -> poisonSpew();
			}
		}
	}
	
	private static final int move1Speed = 30;
	private static final int charge1Speed = 40;
	private static final float moveDurationMax = 5.0f;
	private void tripleRadialBurst() {

		if (MathUtils.randomBoolean()) {
			EnemyUtils.moveToDummy(state, this, "0", move1Speed, moveDurationMax);
			radialBurst("0");
			radialBurst("2");
			radialBurst("1");
			radialBurst("3");
		} else {
			EnemyUtils.moveToDummy(state, this, "4", move1Speed, moveDurationMax);
			radialBurst("4");
			radialBurst("2");
			radialBurst("3");
			radialBurst("1");
		}
	}

	private static final float radialWindup = 0.75f;

	private static final int numShots = 15;

	private static final float shot1Damage = 22.0f;
	private static final float shot1Lifespan = 8.0f;
	private static final float shot1Knockback = 20.0f;
	private static final float shot1Speed = 6.0f;

	private static final Vector2 projSize = new Vector2(40, 40);
	private static final Vector2 projSpriteSize = new Vector2(60, 60);

	Vector2 angle = new Vector2(1, 0);
	private void radialBurst(String dummyId) {
		EnemyUtils.moveToDummy(state, this, dummyId, charge1Speed, moveDurationMax);
		windupParticle(Particle.DIATOM_TELEGRAPH, HadalColor.VIOLET, 40.0f, radialWindup, radialWindup);

		getActions().add(new EnemyAction(this, 0.0f) {

			@Override
			public void execute() {
				SoundEffect.MAGIC3_BURST.playUniversal(state, getPixelPosition(), 1.1f, 0.75f, false);

				for (int i = 0; i < numShots; i++) {
					angle.setAngleDeg(angle.angleDeg() + 360.0f / numShots);

					Vector2 startVelo = new Vector2(shot1Speed, 0).setAngleDeg(angle.angleDeg());
					RangedHitbox hbox = new RangedHitbox(state, enemy.getPixelPosition(), projSize,	shot1Lifespan, startVelo,
						getHitboxfilter(), true, false, enemy, Sprite.DIATOM_SHOT_A);
					hbox.setSpriteSize(projSpriteSize);
					hbox.setAdjustAngle(true);

					hbox.addStrategy(new ControllerDefault(state, hbox, getBodyData()));
					hbox.addStrategy(new DamageStandard(state, hbox, getBodyData(), shot1Damage, shot1Knockback, DamageTypes.RANGED));

					hbox.addStrategy(new CreateParticles(state, hbox, getBodyData(), Particle.DIATOM_TRAIL_DENSE, 0.0f, particleLinger));
					hbox.addStrategy(new DieParticles(state, hbox, getBodyData(), Particle.DIATOM_IMPACT_SMALL));
					hbox.addStrategy(new ContactUnitSound(state, hbox, getBodyData(), SoundEffect.DAMAGE3, 0.6f, true));
					hbox.addStrategy(new ContactUnitDie(state, hbox, getBodyData()));
					hbox.addStrategy(new ContactWallDie(state, hbox, getBodyData()));
				}
			}
		});
	}

	private static final float vineWindup = 1.5f;
	private static final float vineSpeed = 20.0f;
	private static final int vineNum = 8;
	private static final float vineLifespan = 5.0f;
	private static final int vineBendSpreadMin = 15;
	private static final int vineBendSpreadMax = 30;

	private static final Vector2 vineInvisSize = new Vector2(40, 40);
	private static final Vector2 vineSize = new Vector2(80, 40);
	private static final Vector2 vineSpriteSize = new Vector2(120, 120);

	private static final Vector2 seedSize = new Vector2(45, 30);
	private static final float vineDamage = 18.0f;
	private static final float vineKB = 20.0f;
	private void vineLash() {
		if (MathUtils.randomBoolean()) {
			EnemyUtils.moveToDummy(state, this, "0", charge1Speed, moveDurationMax);
		} else {
			EnemyUtils.moveToDummy(state, this, "4", charge1Speed, moveDurationMax);

		}
		windupParticle(Particle.DIATOM_TELEGRAPH, HadalColor.GREEN, 40.0f, vineWindup, vineWindup);

		getActions().add(new EnemyAction(this, 0.0f) {

			@Override
			public void execute() {
				Vector2 vineVelo = new Vector2(0, vineSpeed).setAngleDeg(attackAngle);
				WeaponUtils.createVine(state, enemy, enemy.getPixelPosition(), vineVelo, vineNum, vineLifespan,
					vineDamage, vineKB, vineBendSpreadMin, vineBendSpreadMax, 2, 1,
					vineInvisSize, vineSize, vineSpriteSize, 2);
			}
	 	});
	}

	private static final float seedWindup = 1.5f;
	private static final int seedMoveSpeed = 15;
	private static final int seedNumber = 9;
	private static final float seedSpeed = 15.0f;
	private static final float seedLifespan = 5.0f;
	private static final int seedVineNum = 12;
	private static final float seedVineLifespan = 4.0f;
	private static final float seedInterval = 0.5f;
	private void seedBomber() {

		if (MathUtils.randomBoolean()) {
			EnemyUtils.moveToDummy(state, this, "0", charge1Speed, moveDurationMax);
			windupParticle(Particle.DIATOM_TELEGRAPH, HadalColor.GREEN, 40.0f, seedWindup, seedWindup);
			sowSeed();
			EnemyUtils.moveToDummy(state, this, "4", seedMoveSpeed, moveDurationMax);
		} else {
			EnemyUtils.moveToDummy(state, this, "4", charge1Speed, moveDurationMax);
			windupParticle(Particle.DIATOM_TELEGRAPH, HadalColor.GREEN, 40.0f, seedWindup, seedWindup);
			sowSeed();
			EnemyUtils.moveToDummy(state, this, "0", seedMoveSpeed, moveDurationMax);
		}
	}

	private void sowSeed() {
		getActions().add(new EnemyAction(this, 0.0f) {

			@Override
			public void execute() {
				for (int i = 0; i < seedNumber; i++) {
					getSecondaryActions().add(new EnemyAction(enemy, seedInterval) {

						@Override
						public void execute() {
							SoundEffect.SPIT.playUniversal(state, getPixelPosition(), 1.2f, 0.5f, false);

							RangedHitbox seed = new RangedHitbox(state, getPixelPosition(), seedSize, seedLifespan, new Vector2(0, seedSpeed),
								getHitboxfilter(), false, false, enemy, Sprite.BEAN);
							seed.setGravity(5.0f);
							seed.setFriction(1.0f);

							seed.addStrategy(new ControllerDefault(state, seed, getBodyData()));
							seed.addStrategy(new FlashNearDeath(state, seed, getBodyData(), 1.0f));
							seed.addStrategy(new HitboxStrategy(state, seed, getBodyData()) {

									 @Override
									 public void die() {
										 WeaponUtils.createVine(state, enemy, hbox.getPixelPosition(), new Vector2(0, vineSpeed), seedVineNum, seedVineLifespan,
											 vineDamage, vineKB, vineBendSpreadMin, vineBendSpreadMax, 2, 1,
											 vineInvisSize, vineSize, vineSpriteSize, 0);
									 }
								 }
							);
						}
					});
				}
			}
		});
	}

	private void poisonSpew() {
		if (MathUtils.randomBoolean()) {
			poisonSpewSingle("1");
			poisonSpewSingle("3");
		} else {
			poisonSpewSingle("3");
			poisonSpewSingle("1");
		}
	}

	private static final float poisonWindup = 0.5f;
	private static final float poisonCooldown = 2.0f;
	private static final float poisonBreathSpeed = 10.0f;
	private static final float poisonCloudSpeed = 20.0f;
	private static final float poison1Lifespan = 5.0f;
	private static final float poison2Lifespan = 2.0f;
	private static final float poisonBreathLifespan = 1.5f;
	private static final float poisonCloudLifespan = 6.0f;
	private static final float poisonDamage = 0.3f;
	private static final float poisonParticleLifespan = 3.0f;
	private static final float poisonParticleInterval = 512.0f;
	private static final Vector2 poisonSize = new Vector2(250, 101);
	private static final Vector2 poisonCloudSize = new Vector2(101, 250);
	private void poisonSpewSingle(String dummyId) {
		EnemyUtils.moveToDummy(state, this, dummyId, move1Speed, moveDurationMax);
		windupParticle(Particle.DIATOM_TELEGRAPH, HadalColor.GREEN, 40.0f, poisonWindup, poisonWindup);

		getActions().add(new EnemyAction(this, 0.0f) {

			@Override
			public void execute() {
				RangedHitbox poison = new RangedHitbox(state, enemy.getPixelPosition(), poisonSize, poison1Lifespan, new Vector2(0, -poisonBreathSpeed),
					getHitboxfilter(), true, false, enemy, Sprite.NOTHING);
				poison.setPassability(Constants.BIT_WALL);
				poison.setSyncDefault(false);
				poison.makeUnreflectable();

				poison.addStrategy(new ControllerDefault(state, poison, getBodyData()));
				poison.addStrategy(new ContactWallDie(state, poison, getBodyData()));
				poison.addStrategy(new CreateSound(state, poison, getBodyData(), SoundEffect.OOZE, 0.8f, true));
				poison.addStrategy(new PoisonTrail(state, poison, getBodyData(), poisonSize, (int) poisonSize.y, poisonDamage, poisonBreathLifespan, getHitboxfilter())
				.setParticle(Particle.POLLEN_POISON, poisonParticleLifespan, poisonParticleInterval));
				poison.addStrategy(new HitboxStrategy(state, poison, getBodyData()) {

						 @Override
						 public void die() {
							 createPoisonWave(1);
							 createPoisonWave(-1);
						 }

						 private void createPoisonWave(int direction) {
							 RangedHitbox hbox = new RangedHitbox(state, poison.getPixelPosition(), poisonSize, poison2Lifespan,
								 new Vector2(direction * poisonCloudSpeed, 0), getHitboxfilter(), false, false, enemy, Sprite.NOTHING);
							 hbox.setSyncDefault(false);
							 hbox.makeUnreflectable();
							 hbox.setGravity(1.0f);

							 hbox.addStrategy(new ControllerDefault(state, hbox, getBodyData()));
							 hbox.addStrategy(new PoisonTrail(state, hbox, getBodyData(), poisonCloudSize, (int) poisonCloudSize.x,
								 poisonDamage, poisonCloudLifespan, getHitboxfilter())
								 .setParticle(Particle.POLLEN_POISON, poisonParticleLifespan, poisonParticleInterval));
						 }
					 }
				);
			}
		});
		windupParticle(Particle.DIATOM_TELEGRAPH, HadalColor.GREEN, 40.0f, poisonCooldown, poisonCooldown);
	}

	private static final float sporeWindup = 1.5f;
	private static final int burstNumber = 4;
	private static final float sporeSpeed = 15.0f;
	private static final float sporeLifespan = 3.0f;
	private static final float sporeFragLifespan = 6.0f;
	private static final float sporeDamage = 18.0f;
	private static final float sporeKB = 5.0f;
	private static final float sporeFragDamage = 5.0f;
	private static final float sporeFragKB = 8.0f;
	private static final float sporeInterval = 1.0f;
	private static final Vector2 sporeSize = new Vector2(80, 80);
	private static final Vector2 sporeFragSize = new Vector2(30, 30);

	private static final float sporeHoming = 50.0f;
	private static final int sporeHomingRadius = 120;

	private static final int sporeFragNumber = 16;
	private static final int sporeSpread = 16;
	private static final float fragSpeed = 40.0f;
	private static final float fragVeloSpread = 0.4f;
	private static final float fragSizeSpread = 0.25f;
	private static final float fragDampen = 2.0f;

	private void sporeBurst() {
		EnemyUtils.moveToDummy(state, this, "2", move1Speed, moveDurationMax);
		windupParticle(Particle.DIATOM_TELEGRAPH, HadalColor.BLUE, 40.0f, sporeWindup, sporeWindup);

		for (int i = 0; i < burstNumber; i++) {
			getActions().add(new EnemyAction(this, sporeInterval) {

				@Override
				public void execute() {
					SoundEffect.SPIT.playUniversal(state, getPixelPosition(), 1.2f, 0.5f, false);

					Vector2 startVelo = new Vector2(0, sporeSpeed).setAngleDeg(getAttackAngle());
					RangedHitbox hbox = new RangedHitbox(state, enemy.getPixelPosition(), sporeSize, sporeLifespan,
						startVelo, getHitboxfilter(), false, false, enemy, Sprite.SPORE_CLUSTER_MILD);
					hbox.setRestitution(1.0f);

					hbox.addStrategy(new ControllerDefault(state, hbox, getBodyData()));
					hbox.addStrategy(new AdjustAngle(state, hbox, getBodyData()));
					hbox.addStrategy(new DamageStandard(state, hbox, getBodyData(), sporeDamage, sporeKB, DamageTypes.RANGED));
					hbox.addStrategy(new HomingUnit(state, hbox, getBodyData(), sporeHoming, sporeHomingRadius));
					hbox.addStrategy(new FlashNearDeath(state, hbox, getBodyData(), 1.0f));
					hbox.addStrategy(new ContactUnitSound(state, hbox, getBodyData(), SoundEffect.DAMAGE3, 0.6f, true));
					hbox.addStrategy(new CreateParticles(state, hbox, getBodyData(), Particle.DIATOM_TRAIL_DENSE, 0.0f, particleLinger));
					hbox.addStrategy(new HitboxStrategy(state, hbox, getBodyData()) {

						private final Vector2 newVelocity = new Vector2();
						private final Vector2 newSize = new Vector2();
						@Override
						public void die() {
							SoundEffect.EXPLOSION_FUN.playUniversal(state, hbox.getPixelPosition(), 1.0f, 0.6f, false);

							for (int i = 0; i < sporeFragNumber; i++) {
								newVelocity.set(hbox.getLinearVelocity()).nor().scl(fragSpeed).scl((ThreadLocalRandom.current().nextFloat() * fragVeloSpread + 1 - fragVeloSpread / 2));
								newSize.set(sporeFragSize).scl((ThreadLocalRandom.current().nextFloat() * fragSizeSpread + 1 - fragSizeSpread / 2));

								RangedHitbox frag = new RangedHitbox(state, hbox.getPixelPosition(), new Vector2(newSize), sporeFragLifespan,
								  new Vector2(newVelocity), getHitboxfilter(), false, false, enemy, Sprite.SPORE_MILD) {

									@Override
									public void create() {
										super.create();
										getBody().setLinearDamping(fragDampen);
									}
								};
								frag.setRestitution(1.0f);

								frag.addStrategy(new ControllerDefault(state, frag, getBodyData()));
								frag.addStrategy(new DamageStandard(state, frag, getBodyData(), sporeFragDamage, sporeFragKB, DamageTypes.RANGED).setStaticKnockback(true));
								frag.addStrategy(new ContactUnitDie(state, frag, getBodyData()));
								frag.addStrategy(new ContactUnitSound(state, frag, getBodyData(), SoundEffect.DAMAGE3, 0.6f, true));
								frag.addStrategy(new Spread(state, frag, getBodyData(), sporeSpread));
						  }
					   }
				   });
				}
			});
		}
	}

	private static final float scytheWindup = 0.5f;
	private static final float scytheCooldown = 2.5f;
	private static final float scytheLifespan = 3.2f;
	private static final float scytheDamage = 5.5f;
	private static final float scytheKB = 2.5f;
	private static final float scytheSpinSpeed = 0.25f;
	private static final float scytheAmplitude = 30.0f;
	private static final float scytheFrequency = 1.2f;
	private static final float scytheSpread = 90.0f;
	private static final float scytheAngleFrequency = 0.8f;
	private static final Vector2 scytheSize = new Vector2(240, 80);
	private void scytheAttack() {
		int rand = MathUtils.random(2);
		if (rand == 0) {
			EnemyUtils.moveToDummy(state, this, "1", move1Speed, moveDurationMax);
		} else if (rand == 1) {
			EnemyUtils.moveToDummy(state, this, "2", move1Speed, moveDurationMax);
		} else {
			EnemyUtils.moveToDummy(state, this, "3", move1Speed, moveDurationMax);
		}
		windupParticle(Particle.DIATOM_TELEGRAPH, HadalColor.BLUE, 40.0f, scytheWindup, scytheWindup);
		scytheSingle(0);
		scytheSingle(90);
		scytheSingle(180);
		scytheSingle(270);
		windupParticle(Particle.DIATOM_TELEGRAPH, HadalColor.BLUE, 40.0f, scytheCooldown, scytheCooldown);
	}

	private void scytheSingle(float startAngle) {
		getActions().add(new EnemyAction(this, 0.0f) {

			@Override
			public void execute() {
				SoundEffect.SLASH.playUniversal(state, getPixelPosition(), 1.2f, 0.5f, false);

				RangedHitbox scythe = new RangedHitbox(state, enemy.getPixelPosition(), scytheSize,
					scytheLifespan, new Vector2(), getHitboxfilter(), true, false, enemy, Sprite.DIATOM_SHOT_B);
				scythe.makeUnreflectable();

				scythe.addStrategy(new ControllerDefault(state, scythe, getBodyData()));
				scythe.addStrategy(new CreateSound(state, scythe, getBodyData(), SoundEffect.WOOSH, 0.4f, true).setPitch(0.6f));
				scythe.addStrategy(new DieParticles(state, scythe, getBodyData(), Particle.DIATOM_IMPACT_SMALL));

				scythe.addStrategy(new HitboxStrategy(state, scythe, getBodyData()) {

					private float delayCount, controllerCount, pulseCount;
					private float timer;
					private float angle;
					private static final float delay = 0.5f;
					private static final float pushInterval = 1 / 60f;
					private static final float pulseInterval = 0.06f;
					private final Vector2 offset = new Vector2();
					private final Vector2 centerPos = new Vector2();
					@Override
					public void controller(float delta) {

						//scythes delay some before moving outward
						if (delayCount < delay) {
							delayCount += delta;
						} else {
							pulseCount += delta;
							timer += delta;
						}
						controllerCount += delta;
						while (controllerCount >= pushInterval) {
							controllerCount -= pushInterval;

							//this makes scythes rotate
							angle += scytheSpinSpeed;
							if (getBody() != null && isAlive()) {

								//scythes move along a sin wave in arc from user
								offset.set(0, scytheAmplitude * MathUtils.sin(timer * scytheFrequency))
									.setAngleDeg(startAngle + scytheSpread * MathUtils.sin(timer * scytheAngleFrequency));
								centerPos.set(enemy.getPosition()).add(offset);
								hbox.setTransform(centerPos, angle);
							} else {
								hbox.die();
							}
						}

						while (pulseCount >= pulseInterval) {
							pulseCount -= pulseInterval;

							Hitbox pulse = new Hitbox(state, hbox.getPixelPosition(), hbox.getSize(), pulseInterval,
								new Vector2(0, 0), enemy.getHitboxfilter(), true, false, enemy, Sprite.NOTHING);
							pulse.setSyncDefault(false);
							pulse.makeUnreflectable();

							pulse.addStrategy(new ControllerDefault(state, pulse, getBodyData()));
							pulse.addStrategy(new DamageStandard(state, pulse, getBodyData(), scytheDamage, scytheKB, DamageTypes.RANGED).setStaticKnockback(true));
							pulse.addStrategy(new FixedToEntity(state, pulse, getBodyData(), scythe, new Vector2(), new Vector2(), true));
							pulse.addStrategy(new ContactUnitSound(state, pulse, getBodyData(), SoundEffect.ZAP, 0.6f, true));
						}
					}
				});
			}
		});
	}

	private static final int orbitalChargeSpeed = 30;
	private static final int orbitalNum = 8;
	private static final float orbitalWindup = 1.5f;
	private static final float orbitalPause = 0.4f;
	private static final float orbitalLifespan = 5.5f;
	private static final float orbitalRange = 8.5f;
	private static final float orbitalDamage = 12.0f;
	private static final float orbitalKB = 12.0f;
	private static final float orbitalSpeed = 240.0f;
	private static final float orbitalRangeIncreaseSpeed = 0.1f;
	private static final float orbitalSpinSpeed = 0.4f;
	private static final Vector2 orbitalSize = new Vector2(80, 80);

	private void orbitalCharge() {
		if (MathUtils.randomBoolean()) {
			EnemyUtils.moveToDummy(state, this, "0", move1Speed, moveDurationMax);
			orbitalChargeSingle();
			windupParticle(Particle.DIATOM_TELEGRAPH, HadalColor.RED, 40.0f, orbitalWindup, orbitalWindup);
			EnemyUtils.moveToDummy(state, this, "1", orbitalChargeSpeed, moveDurationMax);
			windupParticle(Particle.DIATOM_TELEGRAPH, HadalColor.RED, 40.0f, orbitalPause, orbitalPause);
			EnemyUtils.moveToDummy(state, this, "2", orbitalChargeSpeed, moveDurationMax);
			windupParticle(Particle.DIATOM_TELEGRAPH, HadalColor.RED, 40.0f, orbitalPause, orbitalPause);
			EnemyUtils.moveToDummy(state, this, "3", orbitalChargeSpeed, moveDurationMax);
			windupParticle(Particle.DIATOM_TELEGRAPH, HadalColor.RED, 40.0f, orbitalPause, orbitalPause);
			EnemyUtils.moveToDummy(state, this, "4", orbitalChargeSpeed, moveDurationMax);
		} else {
			EnemyUtils.moveToDummy(state, this, "4", move1Speed, moveDurationMax);
			orbitalChargeSingle();
			windupParticle(Particle.DIATOM_TELEGRAPH, HadalColor.RED, 40.0f, orbitalWindup, orbitalWindup);
			EnemyUtils.moveToDummy(state, this, "3", orbitalChargeSpeed, moveDurationMax);
			windupParticle(Particle.DIATOM_TELEGRAPH, HadalColor.RED, 40.0f, orbitalPause, orbitalPause);
			EnemyUtils.moveToDummy(state, this, "2", orbitalChargeSpeed, moveDurationMax);
			windupParticle(Particle.DIATOM_TELEGRAPH, HadalColor.RED, 40.0f, orbitalPause, orbitalPause);
			EnemyUtils.moveToDummy(state, this, "1", orbitalChargeSpeed, moveDurationMax);
			windupParticle(Particle.DIATOM_TELEGRAPH, HadalColor.RED, 40.0f, orbitalPause, orbitalPause);
			EnemyUtils.moveToDummy(state, this, "0", orbitalChargeSpeed, moveDurationMax);
		}
		EnemyUtils.moveToDummy(state, this, "2", orbitalChargeSpeed, moveDurationMax);

	}

	private void orbitalChargeSingle() {
		getActions().add(new EnemyAction(this, 0.0f) {

			private final Vector2 angle = new Vector2(0, orbitalRange);
			@Override
			public void execute() {
				for (int i = 0; i < orbitalNum; i++) {
					angle.setAngleDeg(angle.angleDeg() + 360.0f / orbitalNum);
					RangedHitbox orbital = new RangedHitbox(state, enemy.getPixelPosition(), orbitalSize,
						orbitalLifespan, new Vector2(), getHitboxfilter(), true, false, enemy, Sprite.DIATOM_A);
					orbital.makeUnreflectable();

					orbital.addStrategy(new ControllerDefault(state, orbital, getBodyData()));
					orbital.addStrategy(new DamageStandard(state, orbital, getBodyData(), orbitalDamage, orbitalKB, DamageTypes.RANGED).setStaticKnockback(true).setRepeatable(true));
					orbital.addStrategy(new ContactUnitSound(state, orbital, getBodyData(), SoundEffect.DAMAGE3, 0.6f, true));
					orbital.addStrategy(new HitboxStrategy(state, orbital, getBodyData()) {

						private final Vector2 centerPos = new Vector2();
						private final Vector2 offset = new Vector2();
						private float currentAngle = angle.angleDeg();
						private float controllerCount;
						private float currentRange;
						private float orbitalAngle;
						private static final float pushInterval = 1 / 60f;
						@Override
						public void controller(float delta) {
							controllerCount += delta;
							while (controllerCount >= pushInterval) {
								controllerCount -= pushInterval;
								orbitalAngle += orbitalSpinSpeed;
								if (enemy.getBody() != null && enemy.isAlive()) {
									currentAngle += orbitalSpeed * delta;

									centerPos.set(enemy.getPosition());
									offset.set(0, currentRange).setAngleDeg(currentAngle);
									orbital.setTransform(centerPos.add(offset), orbitalAngle);

									//orbitals start off near boss and expand outwards
									if (currentRange < orbitalRange) {
										currentRange += orbitalRangeIncreaseSpeed;
									}
								} else {
									die();
								}
							}
						}
					});

					if (i == 0) {
						new SoundEntity(state, orbital, SoundEffect.MAGIC25_SPELL, 0.8f, 0.5f, true, true, SoundEntity.soundSyncType.TICKSYNC);
					}
				}
			}
		});
	}
	private static final int shadowChargeSpeed = 45;
	private static final int shadowNum = 12;
	private static final float shadowInterval = 0.05f;
	private static final float shadowLifespan = 5.0f;
	private static final float shadowDamage = 22.0f;
	private static final float shadowKB = 15.0f;
	private static final float shadowDelay = 2.5f;
	private static final float shadowSpeed = 25.0f;
	private static final Vector2 shadowSize = new Vector2(30, 30);

	private void spreadingShadow() {
		if (MathUtils.randomBoolean()) {
			EnemyUtils.moveToDummy(state, this, "0", move1Speed, moveDurationMax);
			windupParticle(Particle.DIATOM_TELEGRAPH, HadalColor.NOTHING, 40.0f, orbitalWindup, orbitalWindup);
			spreadingShadowSingle("2");
			windupParticle(Particle.DIATOM_TELEGRAPH, HadalColor.NOTHING, 40.0f, orbitalWindup, orbitalWindup);
			spreadingShadowSingle("4");
		} else {
			EnemyUtils.moveToDummy(state, this, "4", move1Speed, moveDurationMax);
			windupParticle(Particle.DIATOM_TELEGRAPH, HadalColor.NOTHING, 40.0f, orbitalWindup, orbitalWindup);
			spreadingShadowSingle("2");
			windupParticle(Particle.DIATOM_TELEGRAPH, HadalColor.NOTHING, 40.0f, orbitalWindup, orbitalWindup);
			spreadingShadowSingle("0");
		}
	}

	private void spreadingShadowSingle(String dummyId) {
		getActions().add(new EnemyAction(this, 0.0f) {

			@Override
			public void execute() {
				SoundEffect.DARKNESS2.playUniversal(state, getPixelPosition(), 0.4f, 0.6f, false);

				for (int i = 0; i < shadowNum; i++) {
					final int me = i;
					getSecondaryActions().add(new EnemyAction(enemy, shadowInterval) {

						@Override
						public void execute() {
							RangedHitbox shadow = new RangedHitbox(state, getPixelPosition(), shadowSize, shadowLifespan, new Vector2(),
								getHitboxfilter(), true, false, enemy, Sprite.NOTHING);

							//projectile saves boss velocity
							final Vector2 savedVelo = new Vector2(getLinearVelocity());
							shadow.addStrategy(new ControllerDefault(state, shadow, getBodyData()));

							//if boss is not moving, hbox does nothing
							if (!savedVelo.isZero()) {
								shadow.addStrategy(new DamageStandard(state, shadow, getBodyData(), shadowDamage, shadowKB, DamageTypes.RANGED));
								shadow.addStrategy(new ContactUnitDie(state, shadow, getBodyData()));
								shadow.addStrategy(new ContactWallDie(state, shadow, getBodyData()));
								shadow.addStrategy(new CreateParticles(state, shadow, getBodyData(), Particle.POLLEN_FIRE, 0.0f, 1.0f));
								shadow.addStrategy(new ContactUnitSound(state, shadow, getBodyData(), SoundEffect.DAMAGE3, 0.6f, true));
								shadow.addStrategy((new HitboxStrategy(state, shadow, getBodyData()) {

									private float controller;
									private boolean activated;
									private final Vector2 delayVelo = new Vector2();
									@Override
									public void controller(float delta) {
										controller += delta;

										//upon activation, projectile moves perpendicular to saved velocity
										if ((controller > shadowDelay - me * shadowInterval) && !activated) {
											activated = true;
											delayVelo.set(savedVelo).rotate90(me % 2 == 1 ? 1 : -1).nor().scl(shadowSpeed);
											shadow.setLinearVelocity(delayVelo);

											if (me == shadowNum / 2) {
												SoundEffect.DARKNESS1.playUniversal(state, getPixelPosition(), 0.4f, 0.6f, false);
											}
										}
									}
								}));
							}
						}
					});
				}
			}
		});
		EnemyUtils.moveToDummy(state, this, dummyId, shadowChargeSpeed, moveDurationMax);
	}

	private static final Vector2 windupSize = new Vector2(90, 90);
	private static final float particleLinger = 1.0f;

	private void windupParticle(Particle particle, HadalColor color, float particleScale, float lifespan, float duration) {

		getActions().add(new EnemyAction(this, duration) {

			private final Vector2 addVector = new Vector2();
			@Override
			public void execute() {
				Hitbox hbox1 = new Hitbox(state, getPixelPosition(), windupSize, lifespan, new Vector2(), getHitboxfilter(), true, false, enemy, Sprite.NOTHING);

				hbox1.addStrategy(new ControllerDefault(state, hbox1, getBodyData()));
				hbox1.addStrategy(new CreateParticles(state, hbox1, getBodyData(), particle, 0.0f, particleLinger).setParticleColor(color).setParticleSize(particleScale));
				hbox1.addStrategy(new HitboxStrategy(state, hbox1, getBodyData()) {

					@Override
					public void controller(float delta) {
						if (!enemy.isAlive()) {
							hbox.die();
						} else {
							hbox.setTransform(addVector.set(getPixelPosition()).scl(1.0f / PPM), 0);
						}
					}
				});
			}
		});
	}
}
