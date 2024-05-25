package com.mygdx.hadal.schmucks.entities.enemies;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.battle.EnemyUtils;
import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.constants.MoveState;
import com.mygdx.hadal.constants.Stats;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.Status;

public class BombFish extends EnemySwimming {

	private static final int baseHp = 250;

	private static final int scrapDrop = 2;

	private static final int width = 125;
	private static final int height = 125;

	private static final int hboxWidth = 80;
	private static final int hboxHeight = 75;

	private static final float attackCd = 1.0f;
	private static final float airSpeed = -0.1f;

	private static final float minRange = 0.0f;
	private static final float maxRange = 3.0f;

	private static final float noiseRadius = 2.0f;

	private static final Sprite sprite = Sprite.FISH_BOMB_IDLE;

	public BombFish(PlayState state, Vector2 startPos, float startAngle, short filter) {
		super(state, startPos, new Vector2(width, height), new Vector2(hboxWidth, hboxHeight), sprite, EnemyType.BOMBFISH, startAngle, filter, baseHp, attackCd, scrapDrop);
		EnemyUtils.setSwimmingChaseState(this, 1.0f, minRange, maxRange, 0.0f);
		
		getSwimStrategy().setNoiseRadius(noiseRadius);
		getFloatStrategy().addSprite(MoveState.ANIM1, Sprite.FISH_BOMB_ACTIVATE);
		getFloatStrategy().addSprite(MoveState.ANIM2, Sprite.FISH_BOMB_ACTIVATION);
		getFloatStrategy().addSprite(MoveState.ANIM3, Sprite.FISH_BOMB_DEACTIVATION);
	}
	
	@Override
	public void create() {
		super.create();
		getBodyData().addStatus(new StatChangeStatus(state, Stats.AIR_SPD, airSpeed, getBodyData()));
		getBodyData().addStatus(new Status(state, getBodyData()) {

			@Override
			public void onDeath(BodyData perp, DamageSource source, DamageTag... tags) {
				if (!source.equals(DamageSource.ENEMY_ATTACK) && !perp.equals(inflicted)) {
					SyncedAttack.BOMBFISH_ATTACK.initiateSyncedAttackMulti(state, inflicted.getSchmuck(), new Vector2(),
							new Vector2[] {}, new Vector2[] {});
				}
			}
		});
	}

	private static final float ACTIVATION_TIME = 1.2f;
	private static final float DEACTIVATION_TIME = 0.8f;
	private static final float EXPLODE_TIME = 0.5f;
	private float checkCount, animationCount;
	private static final float EXPLODE_INTERVAL = 0.04f;
	private static final float EXPLODE_CHECK_RADIUS = 8.0f;

	private final boolean[] targetFound = {false};
	private final Vector2 enemyLocation = new Vector2();
	@Override
	public void controller(float delta) {
		super.controller(delta);
		processAttack(delta);
	}

	@Override
	public void clientController(float delta) {
		super.clientController(delta);
		processAttack(delta);
	}

	private void processAttack(float delta) {
		checkCount += delta;
		animationCount += delta;
		if (checkCount > EXPLODE_INTERVAL) {
			checkCount -= EXPLODE_INTERVAL;

			enemyLocation.set(getPosition());

			targetFound[0] = false;

			getWorld().QueryAABB((fixture -> {
						if (fixture.getUserData() instanceof final BodyData bodyData) {
							if (bodyData.getSchmuck().getHitboxFilter() != getHitboxFilter()) {
								targetFound[0] = true;
							}
						}
						return true;
					}), enemyLocation.x - EXPLODE_CHECK_RADIUS, enemyLocation.y - EXPLODE_CHECK_RADIUS,
					enemyLocation.x + EXPLODE_CHECK_RADIUS, enemyLocation.y + EXPLODE_CHECK_RADIUS);
		}

		if (targetFound[0]) {
			if (moveState.equals(MoveState.DEFAULT)) {
				animationCount = 0.0f;
				setMoveState(MoveState.ANIM2);
			} else if (moveState.equals(MoveState.ANIM2)) {
				if (animationCount > ACTIVATION_TIME) {
					animationCount = 0.0f;
					setMoveState(MoveState.ANIM1);
				}
			} else if (moveState.equals(MoveState.ANIM3)) {
				if (animationCount > DEACTIVATION_TIME) {
					animationCount = 0.0f;
					setMoveState(MoveState.ANIM2);
				}
			} else if (moveState.equals(MoveState.ANIM1) && state.isServer()) {
				if (animationCount > EXPLODE_TIME) {
					SyncedAttack.BOMBFISH_ATTACK.initiateSyncedAttackMulti(state, this, new Vector2(),
							new Vector2[] {}, new Vector2[] {});
				}
			}
		} else {
			if (moveState.equals(MoveState.ANIM1)) {
				animationCount = 0.0f;
				setMoveState(MoveState.ANIM3);
			} else if (moveState.equals(MoveState.ANIM2)) {
				if (animationCount > ACTIVATION_TIME) {
					animationCount = 0.0f;
					setMoveState(MoveState.ANIM3);
				}
			} else if (moveState.equals(MoveState.ANIM3)) {
				if (animationCount > DEACTIVATION_TIME) {
					animationCount = 0.0f;
					setMoveState(MoveState.DEFAULT);
				}
			}
		}
	}

	@Override
	public void onClientSync(Object o) {
		MoveState actualMoveState = moveState;
		super.onClientSync(o);

		//bombfish handles animations independently and doesn't sync move state
		this.moveState = actualMoveState;
	}
}
