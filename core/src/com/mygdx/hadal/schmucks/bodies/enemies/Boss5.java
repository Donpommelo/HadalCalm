package com.mygdx.hadal.schmucks.bodies.enemies;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.effects.HadalColor;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.EnemyUtils;
import com.mygdx.hadal.event.SpawnerSchmuck;
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.bodies.hitboxes.RangedHitbox;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.strategies.HitboxStrategy;
import com.mygdx.hadal.strategies.hitbox.*;
import com.mygdx.hadal.utils.Stats;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

/**
 * This is a boss in the game
 */
public class Boss5 extends EnemyFloating {

    private static final float aiAttackCd = 2.2f;
    private static final float aiAttackCd2 = 1.6f;

    private static final int scrapDrop = 15;

	private static final int width = 200;
	private static final int height = 200;

	private static final int hbWidth = 200;
	private static final int hbHeight = 200;

	private static final float scale = 1.0f;

	private static final int hp = 10000;

	private static final Sprite sprite = Sprite.ORB_BLUE;

	private int phase = 1;
	private static final float phaseThreshold2 = 0.5f;

	public Boss5(PlayState state, Vector2 startPos, short filter, SpawnerSchmuck spawner) {
		super(state, startPos, new Vector2(width, height).scl(scale), new Vector2(hbWidth, hbHeight).scl(scale), sprite, EnemyType.BOSS5, filter, hp, aiAttackCd, scrapDrop, spawner);
	}

	@Override
	public void create() {
		super.create();
		getBodyData().addStatus(new StatChangeStatus(state, Stats.KNOCKBACK_RES, 1.0f, getBodyData()));
	}
	
	@Override
	public void multiplayerScaling(int numPlayers) {
		getBodyData().addStatus(new StatChangeStatus(state, Stats.MAX_HP, 2000 * numPlayers, getBodyData()));
	}
	
	private int attackNum;
	@Override
	public void attackInitiate() {
		attackNum++;
		if (phase == 1) {
			phase1Attack();
		} else if (phase == 2) {

		}
	}
	
	private static final int phase1NumAttacks = 4;
	private static final int phase2NumAttacks = 5;

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

//		if (attackNum % 2 == 0) {
			int nextAttack = attacks1.remove(GameStateManager.generator.nextInt(attacks1.size()));

			switch(nextAttack) {
				case 0:
					tripleRadialBurst();
					break;
				case 1:
					vineLash(true);
					break;
				case 2:
					vineLash(false);
					break;
				case 3:
					sowSeed();
					break;
			}

//		} else {
//			int nextAttack = attacks2.remove(GameStateManager.generator.nextInt(attacks2.size()));
//		}
	}
	
	
	private void phase2Attack() {
		if (attacks1.isEmpty()) {
			for (int i = 0; i < phase2NumAttacks; i++) {
				attacks1.add(i);
			}
		}
		
		int nextAttack = attacks1.remove(GameStateManager.generator.nextInt(attacks1.size()));
	}


	private static final int charge1Speed = 40;
	private static final float moveDurationMax = 5.0f;
	private void tripleRadialBurst() {
		radialBurst("0");
		radialBurst("2");
		radialBurst("1");
		radialBurst("3");
	}

	private static final float radialWindup = 0.5f;

	private static final int numShots = 15;

	private static final float shot1Damage = 20.0f;
	private static final float shot1Lifespan = 5.0f;
	private static final float shot1Knockback = 20.0f;
	private static final float shot1Speed = 8.0f;

	private static final Vector2 projSize = new Vector2(40, 40);
	private static final Vector2 projSpriteSize = new Vector2(60, 60);

	Vector2 angle = new Vector2(1, 0);
	private void radialBurst(String dummyId) {
		EnemyUtils.moveToDummy(state, this, dummyId, charge1Speed, moveDurationMax);
		windupParticle(Particle.FIRE, HadalColor.VIOLET, 40.0f, radialWindup, radialWindup);

		getActions().add(new EnemyAction(this, 0.0f) {

			@Override
			public void execute() {

				for (int i = 0; i < numShots; i++) {
					angle.setAngleDeg(angle.angleDeg() + 360.0f / numShots);

					Vector2 startVelo = new Vector2(shot1Speed, 0).setAngleDeg(angle.angleDeg());
					RangedHitbox
						hbox = new RangedHitbox(state, getProjectileOrigin(startVelo, projSize.x), projSize, shot1Lifespan, startVelo, getHitboxfilter(), true, false, enemy, Sprite.LASER_PURPLE);
					hbox.setSpriteSize(projSpriteSize);
					hbox.setAdjustAngle(true);

					hbox.addStrategy(new ControllerDefault(state, hbox, getBodyData()));
					hbox.addStrategy(new DamageStandard(state, hbox, getBodyData(), shot1Damage, shot1Knockback, DamageTypes.RANGED));

					hbox.addStrategy(new CreateParticles(state, hbox, getBodyData(), Particle.LASER_TRAIL, 0.0f, particleLinger).setParticleColor(
						HadalColor.VIOLET));
					hbox.addStrategy(new ContactUnitParticles(state, hbox, getBodyData(), Particle.LASER_IMPACT).setOffset(true).setParticleColor(
						HadalColor.VIOLET));
					hbox.addStrategy(new ContactUnitSound(state, hbox, getBodyData(), SoundEffect.DAMAGE3, 0.6f, true));
					hbox.addStrategy(new ContactUnitDie(state, hbox, getBodyData()));
					hbox.addStrategy(new ContactWallDie(state, hbox, getBodyData()));
				}
			}
		});
	}

	private static final float vineWindup = 1.5f;
	private static final float vineSpeed = 20.0f;
	private static final float vineGrowth1 = 1.5f;
	private static final float vineGrowth2 = 1.0f;
	private static final float vineLifespan = 3.0f;
	private static final int vineBendSpreadMin = 15;
	private static final int vineBendSpreadMax = 30;
	private static final int vineSplitSpreadMin = 30;
	private static final int vineSplitSpreadMax = 45;
	private void vineLash(boolean split) {
		EnemyUtils.moveToDummy(state, this, "0", charge1Speed, moveDurationMax);
		windupParticle(Particle.BRIGHT, HadalColor.GREEN, 40.0f, vineWindup, vineWindup);

		getActions().add(new EnemyAction(this, 0.0f) {

			@Override
			public void execute() {
				Vector2 vineVelo = new Vector2(0, vineSpeed).setAngleDeg(attackAngle);
				Hitbox vine = createVine(enemy.getPixelPosition(), vineVelo, split ? vineGrowth2 : vineGrowth1, vineLifespan,
					vineBendSpreadMin, vineBendSpreadMax, 2, 1);
				vine.addStrategy(new HitboxStrategy(state, vine, getBodyData()) {

					@Override
					public void die() {
						float newDegrees = hbox.getLinearVelocity().angleDeg() + (ThreadLocalRandom.current().nextInt(vineSplitSpreadMin, vineSplitSpreadMax));
						angle.set(0, vineSpeed).setAngleDeg(newDegrees);
						Hitbox split1 = createVine(hbox.getPixelPosition(), angle, split ? vineGrowth2 : vineGrowth1, vineLifespan,
							vineBendSpreadMin, vineBendSpreadMax, 2, 1);

						newDegrees = hbox.getLinearVelocity().angleDeg() - (ThreadLocalRandom.current().nextInt(vineSplitSpreadMin, vineSplitSpreadMax));
						angle.set(0, vineSpeed).setAngleDeg(newDegrees);
						Hitbox split2 = createVine(hbox.getPixelPosition(), angle, split ? vineGrowth2 : vineGrowth1, vineLifespan,
							vineBendSpreadMin, vineBendSpreadMax, 2, 1);

						if (split) {
							extraSplit(split1);
							extraSplit(split2);
						}
					}

					private void extraSplit(Hitbox hbox) {
						hbox.addStrategy(new HitboxStrategy(state, hbox, getBodyData()) {

							   @Override
							   public void die() {
								   float newDegrees = hbox.getLinearVelocity().angleDeg() + (ThreadLocalRandom.current().nextInt(vineSplitSpreadMin, vineSplitSpreadMax));
								   angle.set(0, vineSpeed).setAngleDeg(newDegrees);
								   createVine(hbox.getPixelPosition(), angle, vineGrowth2, vineLifespan, vineBendSpreadMin, vineBendSpreadMax, 2, 1);

								   newDegrees = hbox.getLinearVelocity().angleDeg() - (ThreadLocalRandom.current().nextInt(vineSplitSpreadMin, vineSplitSpreadMax));
								   angle.set(0, vineSpeed).setAngleDeg(newDegrees);
								   createVine(hbox.getPixelPosition(), angle, vineGrowth2, vineLifespan, vineBendSpreadMin, vineBendSpreadMax, 2, 1);
							   }
						   }
						);
					}
				});
			}
	 });
	}

	private static final float seedWindup = 1.5f;
	private static final int seedMoveSpeed = 18;
	private static final int seedNumber = 6;
	private static final float seedSpeed = 15.0f;
	private static final float seedLifespan = 5.0f;
	private static final float seedVineGrowth = 1.5f;
	private static final float seedVineLifespan = 4.0f;
	private static final float seedInterval = 0.8f;

	private void sowSeed() {
		EnemyUtils.moveToDummy(state, this, "0", charge1Speed, moveDurationMax);
		windupParticle(Particle.BRIGHT, HadalColor.GREEN, 40.0f, seedWindup, seedWindup);
		getActions().add(new EnemyAction(this, 0.0f) {

			@Override
			public void execute() {
				for (int i = 0; i < seedNumber; i++) {
					getSecondaryActions().add(new EnemyAction(enemy, seedInterval) {

						@Override
						public void execute() {
							RangedHitbox seed = new RangedHitbox(state, getPixelPosition(), vineSize, seedLifespan, new Vector2(0, seedSpeed),
								getHitboxfilter(), false, false, enemy, Sprite.LASER_GREEN);

							seed.setGravity(5.0f);
							seed.setFriction(1.0f);

							seed.addStrategy(new ControllerDefault(state, seed, getBodyData()));
							seed.addStrategy(new FlashNearDeath(state, seed, getBodyData(), 1.0f));
							seed.addStrategy(new HitboxStrategy(state, seed, getBodyData()) {

									 @Override
									 public void die() {
										 createVine(hbox.getPixelPosition(), new Vector2(0, vineSpeed), seedVineGrowth, seedVineLifespan,
											 vineBendSpreadMin, vineBendSpreadMax, 2, 1);
									 }
								 }
							);
						}
					});
				}
			}
	 	});
		EnemyUtils.moveToDummy(state, this, "4", seedMoveSpeed, moveDurationMax);
	}

	private static final Vector2 vineSize = new Vector2(80, 40);
	private static final Vector2 vineSpriteSize = new Vector2(120, 60);
	private static final float vineDamage = 18.0f;
	private static final float vineKnockback = 20.0f;

	private Hitbox createVine(Vector2 startPosition, Vector2 startVelo, float growthTime, float lifespan, int spreadMin, int spreadMax, int bendLength, int bendSpread) {
		RangedHitbox hbox = new RangedHitbox(state, startPosition, vineSize, growthTime, startVelo, getHitboxfilter(), false, false, this, Sprite.NOTHING);
		hbox.makeUnreflectable();
		hbox.setRestitution(1.0f);

		hbox.addStrategy(new ControllerDefault(state, hbox, getBodyData()));
		hbox.addStrategy(new HitboxStrategy(state, hbox, getBodyData()) {

			private final Vector2 lastPosition = new Vector2(startPosition);
			private final Vector2 entityLocation = new Vector2();
			private int vineCount, nextBend;
			private boolean bendRight;

			@Override
			public void controller(float delta) {
				entityLocation.set(hbox.getPixelPosition());
				if (lastPosition.dst(entityLocation) > vineSize.x) {
					lastPosition.set(entityLocation);

					RangedHitbox vine = new RangedHitbox(state, hbox.getPixelPosition(), vineSize, lifespan, new Vector2(),
						getHitboxfilter(), true, false, creator.getSchmuck(), Sprite.LASER_GREEN) {

						@Override
						public void create() {
							super.create();
							setTransform(getPosition(), (float) (Math.atan2(hbox.getLinearVelocity().y, hbox.getLinearVelocity().x)));
						}
					};
					vine.setSpriteSize(vineSpriteSize);

					vine.addStrategy(new ControllerDefault(state, vine, getBodyData()));
					vine.addStrategy(new DamageStandard(state, vine, getBodyData(), vineDamage, vineKnockback, DamageTypes.RANGED));

					vineCount++;

					if (vineCount >= nextBend) {
						hbox.setLinearVelocity(hbox.getLinearVelocity().rotateDeg((bendRight ? -1 : 1) * ThreadLocalRandom.current().nextInt(spreadMin, spreadMax)));
						bendRight = !bendRight;
						vineCount = 0;
						nextBend = bendLength + (ThreadLocalRandom.current().nextInt(-bendSpread, bendSpread + 1));
					}
				}
			}
		});

		return hbox;
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
							hbox.setTransform(addVector.set(getPixelPosition()).scl(1.0f / 32), 0);
						}
					}
				});
			}
		});
	}
}
