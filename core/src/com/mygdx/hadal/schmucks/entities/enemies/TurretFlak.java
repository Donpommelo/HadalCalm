package com.mygdx.hadal.schmucks.entities.enemies;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.effects.HadalColor;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.battle.EnemyUtils;
import com.mygdx.hadal.event.SpawnerSchmuck;
import com.mygdx.hadal.schmucks.MoveState;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.entities.hitboxes.RangedHitbox;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.strategies.hitbox.ContactUnitLoseDurability;
import com.mygdx.hadal.strategies.hitbox.ContactWallDie;
import com.mygdx.hadal.strategies.hitbox.ControllerDefault;
import com.mygdx.hadal.strategies.hitbox.DamageStandard;

/**
 * A Turret is an immobile enemy that fires towards players in sight.
 * @author Floturlov Frehammer
 */
public class TurretFlak extends Turret {

	private static final int baseHp = 200;
	private static final int scrapDrop = 3;
	
	private static final float aiAttackCd = 0.5f;
	
	private static final float scale = 0.5f;
	
	public TurretFlak(PlayState state, Vector2 startPos, float startAngle, short filter, SpawnerSchmuck spawner) {
		super(state, startPos, EnemyType.TURRET_FLAK, startAngle, filter, baseHp, aiAttackCd, scrapDrop, scale, spawner);
		moveState = MoveState.DEFAULT;
	}
	
	private static final float attackWindup1 = 0.6f;
	private static final float attackWindup2 = 0.2f;
	private static final float attackAnimation = 0.2f;

	private static final int numProj = 6;
	private static final int spread = 10;
	
	private static final float baseDamage = 6.0f;
	private static final float projectileSpeed = 25.0f;
	private static final float knockback = 15.0f;
	private static final Vector2 projectileSize = new Vector2(24, 24);
	private static final float projLifespan = 4.0f;
	
	private final Vector2 startVelo = new Vector2();
	private final Vector2 spreadVelo = new Vector2();
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
				
				getActions().add(new EnemyAction(this, 0.0f) {
					
					@Override
					public void execute() {
						
						if (index == 0) {
							SoundEffect.SHOTGUN.playUniversal(state, enemy.getPixelPosition(), 0.75f, 0.75f, false);
						}
						
						startVelo.set(projectileSpeed, projectileSpeed).setAngleDeg(getAttackAngle());

						float newDegrees = startVelo.angleDeg() + MathUtils.random(-spread, spread + 1);
						spreadVelo.set(startVelo.setAngleDeg(newDegrees));
						Hitbox hbox = new RangedHitbox(state, enemy.getProjectileOrigin(spreadVelo, size.x), projectileSize, projLifespan, spreadVelo, getHitboxfilter(), true, true, enemy, Sprite.ORB_RED);
						hbox.setGravity(3.0f);
						
						hbox.addStrategy(new ControllerDefault(state, hbox, getBodyData()));
						hbox.addStrategy(new ContactWallDie(state, hbox, getBodyData()));
						hbox.addStrategy(new ContactUnitLoseDurability(state, hbox, getBodyData()));
						hbox.addStrategy(new DamageStandard(state, hbox, getBodyData(), baseDamage, knockback,
								DamageSource.ENEMY_ATTACK, DamageTag.RANGED));
					}
				});
			}

			EnemyUtils.changeMoveState(this, MoveState.DEFAULT, 0.0f);
			EnemyUtils.changeTurretState(this, TurretState.TRACKING, 0.0f, 0.0f);
		} else {
			EnemyUtils.changeTurretState(this, TurretState.STARTING, 0.0f, 0.0f);
		}
	}
}
