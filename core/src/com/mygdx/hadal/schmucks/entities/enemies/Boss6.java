package com.mygdx.hadal.schmucks.entities.enemies;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.IntArray;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.battle.EnemyUtils;
import com.mygdx.hadal.battle.WeaponUtils;
import com.mygdx.hadal.effects.HadalColor;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.entities.hitboxes.RangedHitbox;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.strategies.HitboxStrategy;
import com.mygdx.hadal.strategies.enemy.CreateMultiplayerHpScaling;
import com.mygdx.hadal.strategies.enemy.MovementFloat.FloatingState;
import com.mygdx.hadal.strategies.enemy.TargetNoPathfinding;
import com.mygdx.hadal.strategies.hitbox.*;
import com.mygdx.hadal.constants.Stats;

import static com.mygdx.hadal.constants.Constants.PPM;

/**
 * This is a boss in the game
 */
public class Boss6 extends EnemyFloating {

    private static final float aiAttackCd = 2.0f;
    private static final float aiAttackCd2 = 1.5f;

    private static final int scrapDrop = 15;

	private static final int width = 150;
	private static final int height = 150;

	private static final int hbWidth = 150;
	private static final int hbHeight = 150;

	private static final float scale = 1.0f;

	private static final int hp = 8000;

	private static final Sprite sprite = Sprite.GOLDFISH;

	private int phase = 1;
	private static final float phaseThreshold2 = 0.5f;

	private static final String[][] zones = {
		{"0", "1", "2", "3", "4"},
		{"5", "6", "7", "8", "9"},
		{"10", "11", "12", "13", "14"},
		{"15", "16", "17", "18", "19"},
		{"20", "21", "22", "23", "24"}};

	private int currentX = 2, currentY = 2;

	public Boss6(PlayState state, Vector2 startPos, short filter) {
		super(state, startPos, new Vector2(width, height).scl(scale), new Vector2(hbWidth, hbHeight).scl(scale), sprite, EnemyType.BOSS6,
			filter, hp, aiAttackCd, scrapDrop);
		addStrategy(new CreateMultiplayerHpScaling(state, this, 1400));
		addStrategy(new TargetNoPathfinding(state, this, true));
	}

	@Override
	public void create() {
		super.create();

		body.setFixedRotation(true);
		body.getFixtureList().get(0).setSensor(true);
		getBodyData().addStatus(new StatChangeStatus(state, Stats.KNOCKBACK_RES, 1.0f, getBodyData()));
	}

	private int attackNum;
	@Override
	public void attackInitiate() {
		attackNum++;
		if (phase == 1) {
			if (getBodyData().getCurrentHp() <= phaseThreshold2 * getBodyData().getStat(Stats.MAX_HP)) {
				phase = 2;
				setAttackCd(aiAttackCd2);
			} else {
				phase1Attack();
			}
		} else if (phase == 2) {
			phase2Attack();
		}
	}

	private static final int phase1NumAttacks = 3;
	private static final int phase2NumAttacks = 3;

	//these lists are used to make the boss perform all attacks in its pool before repeating any
	private final IntArray attacks1 = new IntArray();
	private void phase1Attack() {
		if (attacks1.isEmpty()) {
			for (int i = 0; i < phase1NumAttacks; i++) {
				attacks1.add(i);
			}
		}

		if (attackNum % 2 == 0) {
			int nextAttack = attacks1.removeIndex(MathUtils.random(attacks1.size - 1));

			switch (nextAttack) {
				case 0 -> charge(5, MathUtils.randomBoolean());
				case 1 -> spiralAttack(false);
				case 2 -> crossBeam();
			}
		} else {
			chase(8, chase1Interval, chase1Speed, 0, 0, 5);
		}
	}

	private void phase2Attack() {
		if (attacks1.isEmpty()) {
			for (int i = 0; i < phase2NumAttacks; i++) {
				attacks1.add(i);
			}
		}

		if (attackNum % 2 == 0) {
			int nextAttack = attacks1.removeIndex(MathUtils.random(attacks1.size - 1));
			switch (nextAttack) {
				case 0 -> charge(10, MathUtils.randomBoolean());
				case 1 -> spiralAttack(true);
				case 2 -> jesusBeams();
			}
		} else {
			chase(13, chase2Interval, chase2Speed, 0, 0, 4);
		}
	}

	private static final float chaseDamage = 4.5f;
	private static final int chaseKnockback = 12;
	private static final float meleeAttackInterval = 1 / 60.0f;
	private static final int spinSpeed = 40;
	private static final float chaseCd = 0.5f;

	private static final float moveDurationMax = 5.0f;
	private static final int chase1Speed = 16;
	private static final float chase1Interval = 0.8f;
	private static final int chase2Speed = 21;
	private static final float chase2Interval = 0.6f;

	private static final float gridDistance = 224;
	private final Vector2 bossLocation = new Vector2();
	private final Vector2 targetLocation = new Vector2();
	private void chase(int chaseNum, float chaseInterval, int chaseSpeed, int lastX, int lastY, int bombInterval) {

		if (attackTarget == null || chaseNum == 0) {
			return;
		}
		EnemyUtils.changeFloatingState(this, FloatingState.SPINNING, spinSpeed, 0.0f);

		targetLocation.set(attackTarget.getPixelPosition());
		bossLocation.set(getPixelPosition());

		int xMove = 0, yMove = 0;

		if (bossLocation.x - targetLocation.x > gridDistance / 2) {
			yMove = -1;
		} else if (bossLocation.x - targetLocation.x < -gridDistance / 2) {
			yMove = 1;
		}
		if (bossLocation.y - targetLocation.y > gridDistance / 2) {
			xMove = 1;
		} else if (bossLocation.y - targetLocation.y < -gridDistance / 2) {
			xMove = -1;
		}

		if (xMove != 0 && yMove != 0) {
			if (chaseNum % 2 == 0) {
				xMove = 0;
			} else {
				yMove = 0;
			}
		} else {
			if (xMove == 0 && yMove == 0) {
				xMove = lastX;
				yMove = lastY;
			}
		}

		currentX += xMove;
		currentY += yMove;
		currentX = Math.min(4, Math.max(0, currentX));
		currentY = Math.min(4, Math.max(0, currentY));
		EnemyUtils.meleeAttackContinuous(state, this, chaseDamage, meleeAttackInterval, chaseKnockback, chaseInterval);
		EnemyUtils.moveToDummy(state, this, zones[currentX][currentY], chaseSpeed, moveDurationMax);

		final int finalXMove = xMove;
		final int finalYMove = yMove;
		getActions().add(new EnemyAction(this, 0) {

			@Override
			public void execute() {
				if (chaseNum % bombInterval == 0) {
					crossBomb();
				}
				if (chaseNum - 1 <= 0) {
					EnemyUtils.changeFloatingState((EnemyFloating) enemy, FloatingState.TRACKING_PLAYER, 0, 0.0f);
					setAttackCd(chaseCd);
				} else {
					chase(chaseNum - 1, chaseInterval, chaseSpeed, finalXMove, finalYMove, bombInterval);
				}
			}
		});
	}

	private static final float charge1Windup = 1.0f;
	private static final int charge1Speed = 120;
	private static final float chargeDamage = 35.0f;
	private static final float chargeDuration = 0.09f;
	private static final int chargeKnockback = 25;

	private void charge(int chargeNum, boolean horizontal) {
		EnemyUtils.changeFloatingState(this, FloatingState.TRACKING_PLAYER, 0, charge1Windup);

		for (int i = 0; i < chargeNum; i++) {
			int location1 = chooseRandomPoint(horizontal);
			int squaresTraveled;
			final String nextMove;

			if (horizontal) {
				createTrailBetweenPoints(state.getDummyPoint(zones[currentX][currentY]).getPixelPosition(), state.getDummyPoint(zones[location1][currentY]).getPixelPosition());
				squaresTraveled = Math.abs(location1 - currentX);
				currentX = location1;
				nextMove = zones[location1][currentY];
			} else {
				createTrailBetweenPoints(state.getDummyPoint(zones[currentX][currentY]).getPixelPosition(), state.getDummyPoint(zones[currentX][location1]).getPixelPosition());
				squaresTraveled = Math.abs(location1 - currentY);
				currentY = location1;
				nextMove = zones[currentX][location1];
			}
			horizontal = !horizontal;

			final int finalSquaresTraveled = squaresTraveled;
			getActions().add(new EnemyAction(this, moveDurationMax) {

				@Override
				public void execute() {
					Hitbox hbox = new Hitbox(state, getPixelPosition(), getHboxSize(),
						chargeDuration * finalSquaresTraveled, new Vector2(), getHitboxFilter(),
						true, false, enemy, Sprite.NOTHING);
					hbox.makeUnreflectable();

					hbox.addStrategy(new ControllerDefault(state, hbox, getBodyData()));
					hbox.addStrategy(new DamageStandard(state, hbox, getBodyData(), chargeDamage, chargeKnockback,
							DamageSource.ENEMY_ATTACK, DamageTag.MELEE)
						.setStaticKnockback(true).setRepeatable(true));
					hbox.addStrategy(new FixedToEntity(state, hbox, getBodyData(), new Vector2(), new Vector2()).setRotate(true));
					hbox.addStrategy(new ContactUnitSound(state, hbox, getBodyData(), SoundEffect.DAMAGE3, 0.6f, true));

					Event dummy = state.getDummyPoint(nextMove);

					if (dummy != null) {
						enemy.setMovementTarget(dummy, charge1Speed);
					}
				}
			 });
		}
	}

	private static final Vector2 bombSize = new Vector2(120, 120);
	private static final Vector2 waveSize = new Vector2(20, 20);
	private static final float bombLifespan = 6.0f;
	private static final float waveSpeed = 50.0f;
	private static final float bombDamage = 22.0f;
	private static final float bombKB = 15.0f;
	private void crossBomb() {

		Hitbox bomb = new RangedHitbox(state, new Vector2(state.getDummyPoint(zones[currentX][currentY]).getPixelPosition()),
			bombSize, bombLifespan, new Vector2(), getHitboxFilter(),true, false, this, Sprite.NAVAL_MINE);
		bomb.makeUnreflectable();

		bomb.addStrategy(new ControllerDefault(state, bomb, getBodyData()));
		bomb.addStrategy(new CreateParticles(state, bomb, getBodyData(), Particle.RING, 0.0f, particleLinger));
		bomb.addStrategy(new FlashShaderNearDeath(state, bomb, getBodyData(), 1.0f));
		bomb.addStrategy(new HitboxStrategy(state, bomb, getBodyData()) {

			@Override
			public void die() {
				SoundEffect.EXPLOSION9.playUniversal(state, hbox.getPixelPosition(), 0.5f, 0.5f, false);

				WeaponUtils.createExplosion(state, hbox.getPixelPosition(), gridDistance, creator.getSchmuck(),
						bombDamage, bombKB, creator.getSchmuck().getHitboxFilter(), true, DamageSource.ENEMY_ATTACK);
				explode(0);
				explode(90);
				explode(180);
				explode(270);
			}

			private void explode(float startAngle) {
				Hitbox wave = new RangedHitbox(state, new Vector2(bomb.getPixelPosition()).add(new Vector2(0, gridDistance).setAngleDeg(startAngle)),
					waveSize, trailLifespan, new Vector2(0, waveSpeed).setAngleDeg(startAngle),
					getHitboxFilter(),true, false, creator.getSchmuck(), Sprite.NOTHING);
				wave.makeUnreflectable();

				wave.addStrategy(new ControllerDefault(state, wave, getBodyData()));
				wave.addStrategy(new ContactWallDie(state, wave, getBodyData()));
				wave.addStrategy(new TravelDistanceDie(state, wave, getBodyData(), 3 * gridDistance / PPM / 2));
				wave.addStrategy(new HitboxStrategy(state, wave, getBodyData()) {

					private final Vector2 lastPosition = new Vector2();
					private final Vector2 entityLocation = new Vector2();

					@Override
					public void controller(float delta) {
						entityLocation.set(hbox.getPixelPosition());
						if (lastPosition.dst2(entityLocation) > gridDistance * gridDistance) {
							lastPosition.set(entityLocation);
							WeaponUtils.createExplosion(state, hbox.getPixelPosition(), gridDistance,
								creator.getSchmuck(), bombDamage, bombKB, creator.getSchmuck().getHitboxFilter(),
									true, DamageSource.ENEMY_ATTACK);
						}
					}
				});
			}
		});
	}
	private static final float beamWindup = 0.75f;
	private static final int beamNum = 10;
	private static final float beamInterval = 0.1f;

	private void crossBeam() {
		getActions().add(new EnemyAction(this, beamWindup) {

			@Override
			public void execute() {
				createTrailInDirection(getPixelPosition(), 0);
				createTrailInDirection(getPixelPosition(), 90);
				createTrailInDirection(getPixelPosition(), 180);
				createTrailInDirection(getPixelPosition(), 270);
			}
		});

		for (int i = 0; i < beamNum; i++) {
			final int finalI = i;
			getActions().add(new EnemyAction(this, beamInterval) {

				@Override
				public void execute() {

					if (finalI == 0) {
						SoundEffect.ROLLING_ROCKET.playUniversal(state, getPixelPosition(), 0.5f, 2.5f, false);
					}

					singleBeam(0);
					singleBeam(90);
					singleBeam(180);
					singleBeam(270);
				}
			});
		}
	}

	private static final Vector2 laserSize = new Vector2(165, 165);
	private static final float laserSpeed = 60.0f;
	private static final float laserDamage = 9.0f;
	private static final float laserKB = 12.0f;

	private void singleBeam(float startAngle) {
		Hitbox laser = new RangedHitbox(state, new Vector2(state.getDummyPoint(zones[currentX][currentY]).getPixelPosition()), laserSize, trailLifespan,
			new Vector2(0, laserSpeed).setAngleDeg(startAngle), getHitboxFilter(), true, false, this, Sprite.DIATOM_B);

		laser.addStrategy(new ControllerDefault(state, laser, getBodyData()));
		laser.addStrategy(new AdjustAngle(state, laser, getBodyData()));
		laser.addStrategy(new ContactWallDie(state, laser, getBodyData()));
		laser.addStrategy(new DieParticles(state, laser, getBodyData(), Particle.LASER_IMPACT).setParticleColor(HadalColor.BLUE));
		laser.addStrategy(new DamageStandard(state, laser, getBodyData(), laserDamage, laserKB,	DamageSource.ENEMY_ATTACK,
				DamageTag.RANGED, DamageTag.ENERGY));
		laser.addStrategy(new ContactUnitSound(state, laser, getBodyData(), SoundEffect.DAMAGE3, 0.6f, true));
	}

	private static final float spiralWindup = 1.5f;
	private static final float spiralCooldown = 2.5f;

	private void spiralAttack(boolean bonusWave) {
		EnemyUtils.moveToDummy(state, this, zones[2][currentY], chase1Speed, moveDurationMax);
		EnemyUtils.moveToDummy(state, this, zones[2][2], chase1Speed, moveDurationMax);
		currentX = 2;
		currentY = 2;
		EnemyUtils.changeFloatingState(this, FloatingState.TRACKING_PLAYER, 0, spiralWindup);
		boolean direction = MathUtils.randomBoolean();

		getActions().add(new EnemyAction(this, 0) {

			@Override
			public void execute() {
				SoundEffect.MAGIC3_BURST.playUniversal(state, getPixelPosition(), 1.1f, 0.75f, false);

				spiralSingle(0, direction ? 1 : -1);
				spiralSingle(180, direction ? 1 : -1);

				if (bonusWave) {
					spiralSingle(90, direction ? 1 : -1);
					spiralSingle(270, direction ? 1 : -1);
				}
			}
		});
		EnemyUtils.changeFloatingState(this, FloatingState.TRACKING_PLAYER, 0, spiralCooldown);
	}

	private static final Vector2 spiralSize = new Vector2(165, 165);
	private static final float spiralLifespan = 12.0f;
	private static final float spiralSpeed = 20.0f;
	private static final float spiralDamage = 6.0f;
	private static final float spiralKB = 10.0f;
	private void spiralSingle(float angle, int clockwise) {

		Hitbox spiral = new RangedHitbox(state, new Vector2(state.getDummyPoint(zones[currentX][currentY]).getPixelPosition()), spiralSize, spiralLifespan,
			new Vector2(0, spiralSpeed).setAngleDeg(angle), getHitboxFilter(),true, false, this, Sprite.DIATOM_D);
		spiral.makeUnreflectable();

		spiral.addStrategy(new ControllerDefault(state, spiral, getBodyData()));
		spiral.addStrategy(new HitboxStrategy(state, spiral, getBodyData()) {

			private final Vector2 startLocation = new Vector2();
			private float distance = gridDistance * 2;
			private float pulseCount;
			private boolean firstLoop;
			private static final float pulseInterval = 0.06f;
			@Override
			public void create() { this.startLocation.set(hbox.getPixelPosition()); }

			@Override
			public void controller(float delta) {
				if (startLocation.dst2(hbox.getPixelPosition()) >= distance * distance) {

					if (distance >= gridDistance * 8) {
						hbox.die();
					} else {

						if (firstLoop) {
							distance += (gridDistance * 2);
						}
						firstLoop = !firstLoop;
						startLocation.set(hbox.getPixelPosition());
						hbox.setLinearVelocity(hbox.getLinearVelocity().rotate90(clockwise));
					}
				}
				pulseCount += delta;
				while (pulseCount >= pulseInterval) {
					pulseCount -= pulseInterval;

					Hitbox pulse = new Hitbox(state, hbox.getPixelPosition(), hbox.getSize(), pulseInterval,
						new Vector2(0, 0), getHitboxFilter(), true, false, creator.getSchmuck(), Sprite.NOTHING);
					pulse.setSyncDefault(false);
					pulse.makeUnreflectable();

					pulse.addStrategy(new ControllerDefault(state, pulse, getBodyData()));
					pulse.addStrategy(new DamageStandard(state, pulse, getBodyData(), spiralDamage, spiralKB,
							DamageSource.ENEMY_ATTACK, DamageTag.RANGED).setStaticKnockback(true));
					pulse.addStrategy(new FixedToEntity(state, pulse, getBodyData(), spiral, new Vector2(), new Vector2()).setRotate(true));
					pulse.addStrategy(new ContactUnitSound(state, pulse, getBodyData(), SoundEffect.ZAP, 0.6f, true));
				}
			}
		});
	}

	private static final float pillarWindup = 1.0f;
	private static final float pillarCooldown = 2.5f;
	private static final float spawnerLifespan = 4.0f;
	private static final Vector2 spawnerSize = new Vector2(150, 150);
	private static final Vector2 pillarSize = new Vector2(40, 40);
	private static final float spawnerDelay = 1.0f;
	private static final float pillarInterval = 0.05f;
	private static final float pillarSpeed = 60.0f;
	private static final float pillarDamage = 6.0f;
	private static final float pillarKB = 5.0f;
	private static final int pillarSpread = 60;

	private void jesusBeams() {
		EnemyUtils.moveToDummy(state, this, zones[0][currentY], chase1Speed, moveDurationMax);
		EnemyUtils.moveToDummy(state, this, zones[0][0], chase1Speed, moveDurationMax);
		currentX = 0;
		currentY = 0;
		EnemyUtils.changeFloatingState(this, FloatingState.TRACKING_PLAYER, 0, pillarWindup);
		boolean direction = MathUtils.randomBoolean();

		for (int i = 0; i < 5; i++) {
			if (direction) {
				EnemyUtils.moveToDummy(state, this, zones[0][i], charge1Speed, moveDurationMax);
				currentY = i;
			} else {
				EnemyUtils.moveToDummy(state, this, zones[i][0], charge1Speed, moveDurationMax);
				currentX = i;
			}

			getActions().add(new EnemyAction(this, 0) {

				@Override
				public void execute() {
					createTrailInDirection(getPixelPosition(), direction ? 270 : 0);

					Hitbox spawner = new Hitbox(state, getPixelPosition(), spawnerSize, spawnerLifespan, new Vector2(), getHitboxFilter(),
						true, false, enemy, Sprite.DIATOM_C);

					spawner.addStrategy(new ControllerDefault(state, spawner, getBodyData()));
					spawner.addStrategy(new HitboxStrategy(state, spawner, getBodyData()) {

						private float controllerCount;
						private boolean activated;
						@Override
						public void controller(float delta) {
							controllerCount += delta;

							if (controllerCount > spawnerDelay) {

								if (!activated) {
									activated = true;
								}

								//after a delay, each cloud shoots a stream of ice outwards
								while (controllerCount >= spawnerDelay + pillarInterval) {
									controllerCount -= pillarInterval;

									Vector2 positionOffset = new Vector2(spawner.getPixelPosition())
										.add(MathUtils.random(-pillarSpread, pillarSpread + 1),	MathUtils.random(-pillarSpread, pillarSpread + 1));

									Hitbox hbox = new RangedHitbox(state, positionOffset, pillarSize, spawnerLifespan,
										new Vector2(0, pillarSpeed).setAngleDeg(direction ? 270 : 0), getHitboxFilter(),
										true, false, enemy, Sprite.SPORE);

									hbox.addStrategy(new ControllerDefault(state, hbox, getBodyData()));
									hbox.addStrategy(new DamageStandard(state, hbox, getBodyData(), pillarDamage, pillarKB,
											DamageSource.ENEMY_ATTACK, DamageTag.RANGED));
									hbox.addStrategy(new ContactWallDie(state, hbox, getBodyData()));
									hbox.addStrategy(new ContactUnitSound(state, hbox, getBodyData(), SoundEffect.DAMAGE3, 0.6f, true));
								}
							}
						}
					});
				}
			});
		}

		EnemyUtils.changeFloatingState(this, FloatingState.TRACKING_PLAYER, 0, pillarCooldown);
	}

	private static final float particleLinger = 1.0f;

	private static final float trailInterval = 0.1f;

	private static final Vector2 trailSize = new Vector2(40, 20);
	private static final float trailSpeed = 200.0f;
	private static final float trailLifespan = 10.0f;

	private void createTrailBetweenPoints(Vector2 startPosition, Vector2 endPosition) {

		getSecondaryActions().add(new EnemyAction(this, trailInterval) {

			@Override
			public void execute() {

				Vector2 startVeloTrail = new Vector2(0, trailSpeed).setAngleDeg(new Vector2(endPosition).sub(startPosition).angleDeg());

				Hitbox trail = new RangedHitbox(state, startPosition, trailSize, trailLifespan, startVeloTrail, getHitboxFilter(),
					true, false, enemy, Sprite.NOTHING);
				trail.makeUnreflectable();

				trail.addStrategy(new ControllerDefault(state, trail, getBodyData()));
				trail.addStrategy(new AdjustAngle(state, trail, getBodyData()));
				trail.addStrategy(new CreateParticles(state, trail, getBodyData(), Particle.LASER_TRAIL_SLOW, 0.0f, particleLinger).setParticleSize(15.0f));
				trail.addStrategy(new TravelDistanceDie(state, trail, getBodyData(), endPosition.dst(startPosition) / PPM));
			}
		});
	}

	private void createTrailInDirection(Vector2 startPosition, float startAngle) {

		Vector2 startVeloTrail = new Vector2(0, trailSpeed).setAngleDeg(startAngle);

		Hitbox trail = new RangedHitbox(state, startPosition, trailSize, trailLifespan, startVeloTrail, getHitboxFilter(),
			true, false, this, Sprite.NOTHING);
		trail.makeUnreflectable();

		trail.addStrategy(new ControllerDefault(state, trail, getBodyData()));
		trail.addStrategy(new AdjustAngle(state, trail, getBodyData()));
		trail.addStrategy(new ContactWallDie(state, trail, getBodyData()));
		trail.addStrategy(new CreateParticles(state, trail, getBodyData(), Particle.LASER_TRAIL_SLOW, 0.0f, particleLinger).setParticleSize(15.0f));
	}

	private int chooseRandomPoint(boolean horizontal) {
		int[] options;
		switch (horizontal ? currentX : currentY) {
			case 0 -> {
				options = new int[] {2, 3, 4};
				return options[MathUtils.random(options.length - 1)];
			}
			case 1 -> {
				options = new int[] {3, 4};
				return options[MathUtils.random(options.length - 1)];
			}
			case 2 -> {
				options = new int[] {0, 4};
				return options[MathUtils.random(options.length - 1)];
			}
			case 3 -> {
				options = new int[] {0, 1};
				return options[MathUtils.random(options.length - 1)];
			}
			case 4 -> {
				options = new int[] {0, 1, 2};
				return options[MathUtils.random(options.length - 1)];
			}
		}
		return 2;
	}
}
