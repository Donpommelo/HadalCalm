package com.mygdx.hadal.schmucks.bodies.enemies;

import java.util.concurrent.ThreadLocalRandom;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.EnemyUtils;
import com.mygdx.hadal.event.SpawnerSchmuck;
import com.mygdx.hadal.schmucks.MoveState;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.bodies.hitboxes.RangedHitbox;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.strategies.hitbox.ContactUnitLoseDurability;
import com.mygdx.hadal.strategies.hitbox.ContactWallDie;
import com.mygdx.hadal.strategies.hitbox.ControllerDefault;
import com.mygdx.hadal.strategies.hitbox.DamageStandard;

/**
 * A Turret is an immobile enemy that fires towards players in sight.
 * @author Zachary Tu
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
	
	private static final float attackWindup = 0.75f;
	private static final float attackAnimation = 0.2f;

	private final static int numProj = 6;
	private final static int spread = 10;
	
	private final static float baseDamage = 5.0f;
	private final static float projectileSpeed = 25.0f;
	private final static float knockback = 15.0f;
	private final static Vector2 projectileSize = new Vector2(24, 24);
	private final static float projLifespan = 4.0f;
	
	private Vector2 startVelo = new Vector2();
	private Vector2 spreadVelo = new Vector2();
	@Override
	public void attackInitiate() {

		if (attackTarget != null) {
			EnemyUtils.changeTurretState(this, TurretState.FREE, 0.0f, 0.0f);
			EnemyUtils.windupParticles(state, this, attackWindup, Particle.BRIGHT);
			
			EnemyUtils.changeMoveState(state, this, MoveState.ANIM1, attackAnimation);
			animationTime = 0;
			
			for (int i = 0; i < numProj; i++) {
				
				getActions().add(new EnemyAction(this, 0.0f) {
					
					@Override
					public void execute() {
						startVelo.set(projectileSpeed, projectileSpeed).setAngle(getAttackAngle());

						float newDegrees = (float) (startVelo.angle() + (ThreadLocalRandom.current().nextInt(-spread, spread + 1)));
						spreadVelo.set(startVelo.setAngle(newDegrees));
						Hitbox hbox = new RangedHitbox(state, enemy.getProjectileOrigin(spreadVelo, size.x), projectileSize, projLifespan, spreadVelo, getHitboxfilter(), true, true, enemy, Sprite.ORB_RED);
						hbox.setGravity(3.0f);
						
						hbox.addStrategy(new ControllerDefault(state, hbox, getBodyData()));
						hbox.addStrategy(new ContactWallDie(state, hbox, getBodyData()));
						hbox.addStrategy(new ContactUnitLoseDurability(state, hbox, getBodyData()));
						hbox.addStrategy(new DamageStandard(state, hbox, getBodyData(), baseDamage, knockback, DamageTypes.RANGED));
					}
				});
			}

			EnemyUtils.changeMoveState(state, this, MoveState.DEFAULT, 0.0f);
			EnemyUtils.changeTurretState(this, TurretState.TRACKING, 0.0f, 0.0f);
		} else {
			EnemyUtils.changeTurretState(this, TurretState.STARTING, 0.0f, 0.0f);
		}
	}
}
