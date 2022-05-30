package com.mygdx.hadal.schmucks.entities.enemies;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.battle.EnemyUtils;
import com.mygdx.hadal.schmucks.MoveState;
import com.mygdx.hadal.states.PlayState;

/**
 * A Turret is an immobile enemy that fires towards players in sight.
 * @author Wumquat Wrogma
 */
public class TurretBoss extends Turret {

	private static final int baseHp = 6000;
	private static final int scrapDrop = 30;
	
	private static final float aiAttackCd = 3.0f;
	
	private static final float scale = 1.5f;
	
	public TurretBoss(PlayState state, Vector2 startPos, float startAngle, short filter) {
		super(state, startPos, EnemyType.TURRET_BOSS, startAngle, filter, baseHp, aiAttackCd, scrapDrop, scale);
		moveState = MoveState.DEFAULT;
		setCurrentState(TurretState.TRACKING);
	}
	
	private static final int bulletDamage = 10;
	private static final int bulletSpeed = 12;
	private static final int bulletKB = 35;
	private static final int bulletSize = 60;
	private static final float bulletLifespan = 1.5f;

	private static final int bulletNumber = 8;
	private static final float bulletInterval = 0.25f;
	@Override
	public void attackInitiate() {
		sprayShot();
	}
	
	public void sprayShot() {
		EnemyUtils.changeTurretState(this, TurretState.FREE, 180.0f, 1.0f);
		for (int i = 0; i < bulletNumber; i++) {
			EnemyUtils.changeTurretState(this, TurretState.FREE, 180.0f - i * 10, 0);
			EnemyUtils.changeMoveState(this, MoveState.ANIM1, 0.2f);
			animationTime = 0;
			EnemyUtils.changeMoveState(this, MoveState.DEFAULT, 0);
			EnemyUtils.shootBullet(state, this, bulletDamage, bulletSpeed, bulletKB, bulletSize, bulletLifespan, bulletInterval);
		}
		EnemyUtils.changeTurretState(this, TurretState.TRACKING, 0.0f, 0.0f);
	}
}
