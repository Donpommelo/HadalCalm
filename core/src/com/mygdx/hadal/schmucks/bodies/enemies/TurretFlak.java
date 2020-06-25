package com.mygdx.hadal.schmucks.bodies.enemies;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.equip.EnemyUtils;
import com.mygdx.hadal.event.SpawnerSchmuck;
import com.mygdx.hadal.schmucks.MoveState;
import com.mygdx.hadal.states.PlayState;

/**
 * A Turret is an immobile enemy that fires towards players in sight.
 * @author Zachary Tu
 *
 */
public class TurretFlak extends Turret {

	private static final int baseHp = 200;
	private final static String name = "FLAK TURRET";

	private final static int scrapDrop = 3;
	
	private static final float aiAttackCd = 0.5f;
	
	private static final float scale = 0.5f;
	
	public TurretFlak(PlayState state, Vector2 startPos, float startAngle, short filter, SpawnerSchmuck spawner) {
		super(state, startPos, name, EnemyType.TURRET_FLAK, startAngle, filter, baseHp, aiAttackCd, scrapDrop, scale, spawner);		
		moveState = MoveState.DEFAULT;
	}
	
	@Override
	public void controller(float delta) {
		
		if (attackTarget != null) {
			setCurrentState(TurretState.TRACKING);
		} else {
			setCurrentState(TurretState.STARTING);
		}
		
		super.controller(delta);
	}
	
	private final static float baseDamage = 20.0f;
	private final static float projSpeed = 25.0f;
	private final static float knockback = 15.0f;
	private final static int projSize = 40;
	private final static float projLifespan = 4.0f;
	private final static float projInterval = 0.5f;
	@Override
	public void attackInitiate() {
		
		if (getCurrentState().equals(TurretState.TRACKING)) {
			EnemyUtils.changeMoveState(state, this, MoveState.ANIM1, 0.5f);
			animationTime = 0;
			EnemyUtils.shootBullet(state, this, baseDamage, projSpeed, knockback, projSize, projLifespan, projInterval);
			EnemyUtils.changeMoveState(state, this, MoveState.DEFAULT, 0.0f);
		}
	}
}
