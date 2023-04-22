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
 * @author Floturlov Frehammer
 */
public class TurretFlak extends Turret {

	private static final int baseHp = 200;
	private static final int scrapDrop = 3;
	
	private static final float aiAttackCd = 0.5f;
	
	private static final float scale = 0.5f;
	
	public TurretFlak(PlayState state, Vector2 startPos, float startAngle, short filter) {
		super(state, startPos, EnemyType.TURRET_FLAK, startAngle, filter, baseHp, aiAttackCd, scrapDrop, scale);
		moveState = MoveState.DEFAULT;
	}
	
	private static final float ATTACK_WINDUP_1 = 0.6f;
	private static final float ATTACK_WINDUP_2 = 0.2f;
	private static final float ATTACK_ANIMATION = 0.2f;

	private static final int NUM_PROJ = 6;
	private static final float PROJECTILE_SPEED = 25.0f;

	private final Vector2 startVelocity = new Vector2();
	private final Vector2 startPosition = new Vector2();
	@Override
	public void attackInitiate() {

		if (attackTarget != null) {
			EnemyUtils.windupParticles(state, this, ATTACK_WINDUP_1, Particle.CHARGING, HadalColor.RED, 80.0f);
			EnemyUtils.changeTurretState(this, TurretState.FREE, 0.0f, 0.0f);
			EnemyUtils.windupParticles(state, this, ATTACK_WINDUP_2, Particle.OVERCHARGE, HadalColor.RED, 80.0f);
			
			EnemyUtils.changeMoveState(this, MoveState.ANIM1, ATTACK_ANIMATION);
			animationTime = 0;

			getActions().add(new EnemyAction(this, 0.0f) {

				@Override
				public void execute() {
					startVelocity.set(PROJECTILE_SPEED, PROJECTILE_SPEED).setAngleDeg(getAttackAngle());
					startPosition.set(enemy.getProjectileOrigin(startVelocity, size.x));
					Vector2[] positions = new Vector2[NUM_PROJ];
					Vector2[] velocities = new Vector2[NUM_PROJ];
					for (int i = 0; i < NUM_PROJ; i++) {
						positions[i] = startPosition;
						velocities[i] = startVelocity;
					}
					SyncedAttack.TURRET_FLAK_ATTACK.initiateSyncedAttackMulti(state, enemy, startVelocity, positions, velocities);
				}
			});

			EnemyUtils.changeMoveState(this, MoveState.DEFAULT, 0.0f);
			EnemyUtils.changeTurretState(this, TurretState.TRACKING, 0.0f, 0.0f);
		} else {
			EnemyUtils.changeTurretState(this, TurretState.STARTING, 0.0f, 0.0f);
		}
	}
}
