package com.mygdx.hadal.schmucks.entities.enemies;


import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.battle.EnemyUtils;
import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.constants.MoveState;
import com.mygdx.hadal.effects.HadalColor;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.states.PlayState;

/**
 * A Turret is an immobile enemy that fires towards players in sight.
 * @author Thadding Thurrasco
 */
public class TurretVolley extends Turret {

	private static final int baseHp = 200;
	private static final int scrapDrop = 3;
	
	private static final float aiAttackCd = 0.5f;
	
	private static final float scale = 0.5f;
	
	public TurretVolley(PlayState state, Vector2 startPos, float startAngle, short filter) {
		super(state, startPos, EnemyType.TURRET_VOLLEY, startAngle, filter, baseHp, aiAttackCd, scrapDrop, scale);
		moveState = MoveState.DEFAULT;
	}
	
	private static final int numProj = 3;
	
	private static final float attackWindup1 = 0.6f;
	private static final float attackWindup2 = 0.2f;
	private static final float attackAnimation = 0.2f;
	
	private static final float projectileSpeed = 35.0f;
	private static final float projInterval = 0.25f;
	
	private final Vector2 startVelo = new Vector2();
	@Override
	public void attackInitiate() {
		
		if (attackTarget != null) {
			EnemyUtils.windupParticles(state, this, attackWindup1, Particle.CHARGING, HadalColor.RED, 80.0f);
			EnemyUtils.changeTurretState(this, TurretState.FREE, 0.0f, 0.0f);
			EnemyUtils.windupParticles(state, this, attackWindup2, Particle.OVERCHARGE, HadalColor.RED, 80.0f);
			
			EnemyUtils.changeMoveState(this, MoveState.ANIM1, attackAnimation);
			animationTime = 0;
			
			for (int i = 0; i < numProj; i++) {
				
				final int index = i;
				
				getActions().add(new EnemyAction(this, projInterval) {
					
					@Override
					public void execute() {
						startVelo.set(projectileSpeed, projectileSpeed).setAngleDeg(getAttackAngle());
						SyncedAttack.TURRET_VOLLEY_ATTACK.initiateSyncedAttackSingle(state, enemy, enemy.getProjectileOrigin(startVelo, size.x), startVelo, index);
					}
				});
			}

			EnemyUtils.changeMoveState(this, MoveState.DEFAULT, 0.0f);
			EnemyUtils.changeTurretState(this, TurretState.TRACKING, 0.0f, 0.0f);
		} else {
			EnemyUtils.changeTurretState(this, TurretState.STARTING, 0.0f, 0.0f);
		}
	}
	
	private final Vector2 originPt = new Vector2();
	private final Vector2 addVelo = new Vector2();
	private static final float spawnDist = 300.0f;
	@Override
	public Vector2 getProjectileOrigin(Vector2 startVelo, float projSize) {
		originPt.set(getPixelPosition()).add(addVelo.set(startVelo).nor().scl(scale * spawnDist)).add(0, 40);
		return originPt;
	}
}
