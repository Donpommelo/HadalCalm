package com.mygdx.hadal.schmucks.bodies.enemies;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.EnemyUtils;
import com.mygdx.hadal.event.SpawnerSchmuck;
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.bodies.hitboxes.RangedHitbox;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.strategies.hitbox.AdjustAngle;
import com.mygdx.hadal.strategies.hitbox.ControllerDefault;
import com.mygdx.hadal.strategies.hitbox.CreateParticles;
import com.mygdx.hadal.strategies.hitbox.TravelDistanceDie;
import com.mygdx.hadal.utils.Stats;

import java.util.ArrayList;

/**
 * This is a boss in the game
 */
public class Boss6 extends EnemyFloating {

    private static final float aiAttackCd = 2.1f;
    private static final float aiAttackCd2 = 1.6f;

    private static final int scrapDrop = 15;

	private static final int width = 100;
	private static final int height = 100;

	private static final int hbWidth = 120;
	private static final int hbHeight = 120;

	private static final float scale = 1.0f;

	private static final int hp = 8000;

	private static final Sprite sprite = Sprite.ORB_BLUE;

	private int phase = 1;
	private static final float phaseThreshold2 = 0.0f;

	private static final String[][] zones = {
		{"0", "1", "2", "3", "4"},
		{"5", "6", "7", "8", "9"},
		{"10", "11", "12", "13", "14"},
		{"15", "16", "17", "18", "19"},
		{"20", "21", "22", "23", "24"}};

	private int currentX = 2, currentY = 2;

	public Boss6(PlayState state, Vector2 startPos, short filter, SpawnerSchmuck spawner) {
		super(state, startPos, new Vector2(width, height).scl(scale), new Vector2(hbWidth, hbHeight).scl(scale), sprite, EnemyType.BOSS5,
			filter, hp, aiAttackCd, scrapDrop, spawner);
		trackThroughWalls = true;
	}

	@Override
	public void create() {
		super.create();

		body.setFixedRotation(true);
		body.getFixtureList().get(0).setSensor(true);
		getBodyData().addStatus(new StatChangeStatus(state, Stats.KNOCKBACK_RES, 1.0f, getBodyData()));
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

		chase(20, 0, 0);

//		if (attackNum % 2 == 0) {
//			int nextAttack = attacks1.remove(GameStateManager.generator.nextInt(attacks1.size()));
//
//			switch(nextAttack) {
//				case 0:
//				case 1:
//				case 2:
//				case 3:
//					charge(5, true);
//					break;
//			}
//
//		} else {
//			int nextAttack = attacks2.remove(GameStateManager.generator.nextInt(attacks2.size()));
//			switch(nextAttack) {
//				case 0:
//				case 1:
//				case 2:
//				case 3:
//					charge(5, false);
//					break;
//			}
//		}
	}
	private static final float moveDurationMax = 5.0f;
	private static final int chase1Speed = 25;

	private static final float gridDistance = 224;
	private final Vector2 bossLocation = new Vector2();
	private final Vector2 targetLocation = new Vector2();
	private void chase(int chaseNum, int lastX, int lastY) {

		if (attackTarget == null || chaseNum == 0) { return; }

		targetLocation.set(attackTarget.getPixelPosition());
		bossLocation.set(getPixelPosition());

		int xMove = 0, yMove = 0;

		if (bossLocation.x - targetLocation.x > gridDistance) {
			yMove = -1;
		} else if (bossLocation.x - targetLocation.x < -gridDistance) {
			yMove = 1;
		}
		if (bossLocation.y - targetLocation.y > gridDistance) {
			xMove = 1;
		} else if (bossLocation.y - targetLocation.y < -gridDistance) {
			xMove = -1;
		}

		if (xMove != 0 && yMove != 0) {
			if (chaseNum % 2 == 0) {
				currentY += yMove;
				xMove = 0;
			} else {
				currentX += xMove;
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
		EnemyUtils.moveToDummy(state, this, zones[currentX][currentY], chase1Speed, moveDurationMax);

		final int finalXMove = xMove;
		final int finalYMove = yMove;
		getActions().add(new EnemyAction(this, 0) {

			@Override
			public void execute() {
				chase(chaseNum - 1, finalXMove, finalYMove);
			}
		});
	}

	private static final float charge1Windup = 1.0f;
	private static final int charge1Speed = 150;

	private void charge(int chargeNum, boolean horizontal) {
		EnemyUtils.changeFloatingState(this, FloatingState.TRACKING_PLAYER, 0, charge1Windup);

		for (int i = 0; i < chargeNum; i++) {
			int location1 = chooseRandomPoint(horizontal);

			if (horizontal) {
				createTrail(state.getDummyPoint(zones[currentX][currentY]).getPixelPosition(), state.getDummyPoint(zones[location1][currentY]).getPixelPosition());

				currentX = location1;

				EnemyUtils.moveToDummy(state, this, zones[location1][currentY], charge1Speed, moveDurationMax);
			} else {
				createTrail(state.getDummyPoint(zones[currentX][currentY]).getPixelPosition(), state.getDummyPoint(zones[currentX][location1]).getPixelPosition());

				currentY = location1;

				EnemyUtils.moveToDummy(state, this, zones[currentX][location1], charge1Speed, moveDurationMax);
			}

			horizontal = !horizontal;
		}
	}

	private static final float particleLinger = 1.0f;

	private static final int trailNumber = 1;
	private static final float trailInterval = 0.1f;

	private static final Vector2 trailSize = new Vector2(120, 60);
	private static final float trailSpeed = 200.0f;
	private static final float trailLifespan = 10.0f;

	private void createTrail(Vector2 startPosition, Vector2 endPosition) {

		for (int i = 0; i < trailNumber; i++) {
			getSecondaryActions().add(new EnemyAction(this, trailInterval) {

				@Override
				public void execute() {

					Vector2 startVeloTrail = new Vector2(0, trailSpeed).setAngleDeg(new Vector2(endPosition).sub(startPosition).angleDeg());

					Hitbox trail = new RangedHitbox(state, startPosition, trailSize, trailLifespan, startVeloTrail, getHitboxfilter(), true, false, enemy, Sprite.NOTHING);

					trail.addStrategy(new ControllerDefault(state, trail, getBodyData()));
					trail.addStrategy(new AdjustAngle(state, trail, getBodyData()));
					trail.addStrategy(new CreateParticles(state, trail, getBodyData(), Particle.LASER_TRAIL_SLOW, 0.0f, particleLinger).setParticleSize(40.0f));
					trail.addStrategy(new TravelDistanceDie(state, trail, getBodyData(), endPosition.dst(startPosition) / 32));
				}
			});
		}
	}

	private int chooseRandomPoint(boolean horizontal) {
		int[] options;
		switch (horizontal ? currentX : currentY) {
			case 0:
				options = new int[] {2, 3, 4};
				return options[GameStateManager.generator.nextInt(options.length)];
			case 1:
				options = new int[] {3, 4};
				return options[GameStateManager.generator.nextInt(options.length)];
			case 2:
				options = new int[] {0, 4};
				return options[GameStateManager.generator.nextInt(options.length)];
			case 3:
				options = new int[] {0, 1};
				return options[GameStateManager.generator.nextInt(options.length)];
			case 4:
				options = new int[] {0, 1, 2};
				return options[GameStateManager.generator.nextInt(options.length)];
		}
		return 2;
	}

	private int chooseRandomAdjacency(boolean horizontal) {
		int[] options;
		switch (horizontal ? currentX : currentY) {
			case 0:
				return 1;
			case 1:
				options = new int[] {0, 2};
				return options[GameStateManager.generator.nextInt(options.length)];
			case 2:
				options = new int[] {1, 3};
				return options[GameStateManager.generator.nextInt(options.length)];
			case 3:
				options = new int[] {2, 4};
				return options[GameStateManager.generator.nextInt(options.length)];
			case 4:
				return 3;
		}
		return 2;
	}
}
