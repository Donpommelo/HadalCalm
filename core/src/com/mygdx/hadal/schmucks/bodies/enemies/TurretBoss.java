package com.mygdx.hadal.schmucks.bodies.enemies;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.event.SpawnerSchmuck;
import com.mygdx.hadal.schmucks.MoveState;
import com.mygdx.hadal.states.PlayState;

/**
 * A Turret is an immobile enemy that fires towards players in sight.
 * @author Zachary Tu
 *
 */
public class TurretBoss extends Turret {

	private static final int baseHp = 6000;
	private static final float aiAttackCd = 0.5f;
	
	private static final float scale = 1.5f;
	
	public TurretBoss(PlayState state, Vector2 startPos, float startAngle, short filter, SpawnerSchmuck spawner) {
		super(state, startPos, EnemyType.TURRET_FLAK, startAngle, filter, baseHp, aiAttackCd, scale, spawner);		
		moveState = MoveState.DEFAULT;
		setCurrentState(TurretState.TRACKING);
	}
	

	@Override
	public void attackInitiate() {

	}
}
