package com.mygdx.hadal.schmucks.entities.enemies;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.IntArray;
import com.mygdx.hadal.battle.EnemyUtils;
import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.constants.Stats;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.strategies.enemy.CreateMultiplayerHpScaling;
import com.mygdx.hadal.strategies.enemy.MovementFloat.FloatingState;
import com.mygdx.hadal.strategies.enemy.TargetNoPathfinding;

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
					SyncedAttack.SERAPH_BOMB.initiateSyncedAttackSingle(state, enemy,
							state.getDummyPoint(zones[currentX][currentY]).getPixelPosition(), new Vector2());
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
	private static final float chargeDuration = 0.09f;

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
					SyncedAttack.SERAPH_CHARGE.initiateSyncedAttackSingle(state, enemy, new Vector2(), new Vector2(),
							finalSquaresTraveled * chargeDuration, enemy.getHboxSize().x, enemy.getHboxSize().y);

					Event dummy = state.getDummyPoint(nextMove);
					if (dummy != null) {
						enemy.setMovementTarget(dummy, charge1Speed);
					}
				}
			 });
		}
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
					SyncedAttack.SERAPH_CROSS.initiateSyncedAttackMulti(state, enemy, new Vector2(),
							new Vector2[] {new Vector2(state.getDummyPoint(zones[currentX][currentY]).getPixelPosition())},
							new Vector2[] {}, finalI);
				}
			});
		}
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
				SyncedAttack.SERAPH_SPIRAL.initiateSyncedAttackMulti(state, enemy, new Vector2(),
						new Vector2[] {new Vector2(state.getDummyPoint(zones[currentX][currentY]).getPixelPosition())},
						new Vector2[] {}, bonusWave ? 0.0f : 1.0f, direction ? 1.0f : -1.0f);
			}
		});
		EnemyUtils.changeFloatingState(this, FloatingState.TRACKING_PLAYER, 0, spiralCooldown);
	}

	private static final float pillarWindup = 1.0f;
	private static final float pillarCooldown = 2.5f;

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
					SyncedAttack.SERAPH_LOTUS.initiateSyncedAttackSingle(state, enemy, getPixelPosition(), new Vector2(),
							direction ? 0.0f : 1.0f);
				}
			});
		}

		EnemyUtils.changeFloatingState(this, FloatingState.TRACKING_PLAYER, 0, pillarCooldown);
	}


	private static final float trailInterval = 0.1f;
	private static final float trailSpeed = 200.0f;
	private void createTrailBetweenPoints(Vector2 startPosition, Vector2 endPosition) {

		getSecondaryActions().add(new EnemyAction(this, trailInterval) {

			@Override
			public void execute() {
				Vector2 startVeloTrail = new Vector2(0, trailSpeed).setAngleDeg(new Vector2(endPosition).sub(startPosition).angleDeg());
				SyncedAttack.SERAPH_TRAIL.initiateSyncedAttackSingle(state, enemy, startPosition, startVeloTrail);
			}
		});
	}

	private void createTrailInDirection(Vector2 startPosition, float startAngle) {
		Vector2 startVeloTrail = new Vector2(0, trailSpeed).setAngleDeg(startAngle);
		SyncedAttack.SERAPH_TRAIL.initiateSyncedAttackSingle(state, this, startPosition, startVeloTrail);
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
