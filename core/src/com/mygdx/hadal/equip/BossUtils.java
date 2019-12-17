package com.mygdx.hadal.equip;

import com.mygdx.hadal.schmucks.bodies.enemies.Scissorfish;
import com.mygdx.hadal.schmucks.bodies.enemies.Spittlefish;
import com.mygdx.hadal.schmucks.bodies.enemies.Torpedofish;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Ablaze;
import com.mygdx.hadal.statuses.DamageTypes;

import static com.mygdx.hadal.utils.Constants.PPM;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.schmucks.bodies.HadalEntity;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity.particleSyncType;
import com.mygdx.hadal.schmucks.bodies.enemies.Boss1;
import com.mygdx.hadal.schmucks.bodies.enemies.Boss1.BossState;
import com.mygdx.hadal.schmucks.bodies.enemies.BossAction;
import com.mygdx.hadal.schmucks.bodies.enemies.Enemy.enemyType;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.bodies.hitboxes.MeleeHitbox;
import com.mygdx.hadal.schmucks.bodies.hitboxes.RangedHitbox;
import com.mygdx.hadal.schmucks.strategies.HitboxDamageStandardStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxDefaultStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxOnContactUnitStatusStrategy;
import com.mygdx.hadal.utils.Constants;

public class BossUtils {

	public static void moveToDummy(final PlayState state, final Boss1 boss, final String dummyId, final int speed) {
		
		boss.getActions().add(new BossAction(boss, 10.0f) {
			
			@Override
			public void execute() {
				Event dummy = state.getDummyPoint(dummyId);
				
				if (dummy != null) {
					boss.setMovementTarget(dummy);
					boss.setMoveSpeed(speed);
				}
			}
			
		});
	}
	
	public static void changeTrackingState(Boss1 boss, final BossState state, final float angle, float duration) {
		
		boss.getActions().add(new BossAction(boss, duration) {
			
			@Override
			public void execute() {
				boss.setCurrentState(state);
				boss.setAngle(normalizeAngle((int) boss.getAngle()));
				switch (state) {
				case FREE:
					boss.setDesiredAngle(angle);
				case SPINNING:
					boss.setSpinSpeed((int)angle);
					break;
				case LOCKED:
					boss.setAngle(angle);
					break;
				case TRACKING_PLAYER:
					break;
				default:
					break;
				
				}
			}
		});
	}
	
	public static void spawnAdds(final PlayState state, Boss1 boss, final enemyType type, final int amount, float duration) {
		
		boss.getActions().add(new BossAction(boss, duration) {
			
			@Override
			public void execute() {
				
				for (int i = 0; i < amount; i++) {
					switch (type) {
					case SCISSORFISH:
						new Scissorfish(state, (int)(boss.getPosition().x * PPM), (int)(boss.getPosition().y * PPM), Constants.ENEMY_HITBOX);
						break;
					case SPITTLEFISH:
						new Spittlefish(state, (int)(boss.getPosition().x * PPM), (int)(boss.getPosition().y * PPM), Constants.ENEMY_HITBOX);
						break;
					case TORPEDOFISH:
						new Torpedofish(state, (int)(boss.getPosition().x * PPM), (int)(boss.getPosition().y * PPM), Constants.ENEMY_HITBOX);
						break;
					default:
						break;
					}
				}
			}
		});
	}
	
	public static void moveToPlayer(final PlayState state, Boss1 boss, final HadalEntity target, final int moveSpeed, final float duration) {
		
		boss.getActions().add(new BossAction(boss, duration) {
			
			@Override
			public void execute() {
				Vector2 dist = target.getPosition().sub(boss.getPosition()).scl(PPM);
				boss.setLinearVelocity(dist.nor().scl(moveSpeed));
			}
		});
	}
	
	public static void meleeAttack(final PlayState state, Boss1 boss, final float damage, final float knockback, final HadalEntity target, final float duration) {
		
		boss.getActions().add(new BossAction(boss, 0) {
			
			@Override
			public void execute() {
				
				Vector2 dist = target.getPosition().sub(boss.getPosition()).scl(PPM);
				
				Hitbox hbox = new MeleeHitbox(state, boss.getPosition().x * PPM, boss.getPosition().y * PPM, (int)boss.getHeight(), (int)boss.getWidth(), duration, duration, dist, 
						new Vector2(0, 0), true, boss.getHitboxfilter(), boss);
				
				hbox.addStrategy(new HitboxDefaultStrategy(state, hbox, boss.getBodyData()));
				hbox.addStrategy(new HitboxDamageStandardStrategy(state, hbox, boss.getBodyData(), null, damage, knockback, DamageTypes.MELEE));	
			}
		});
	}

	public static void fireball(final PlayState state, Boss1 boss, final float baseDamage, final float fireDamage, final float projSpeed, final float knockback, final int size, final float gravity, 
			final float lifespan, final float fireDuration, final float duration) {
		
		boss.getActions().add(new BossAction(boss, 0) {
			
			@Override
			public void execute() {
				
				RangedHitbox hbox = new RangedHitbox(state, boss.getPosition().x * PPM, boss.getPosition().y * PPM, size, size, gravity, lifespan, 3, 0, new Vector2(projSpeed, projSpeed).setAngle(boss.getAngle()),
						boss.getHitboxfilter(), false, true, boss);
				
				hbox.addStrategy(new HitboxDefaultStrategy(state, hbox, boss.getBodyData()));
				hbox.addStrategy(new HitboxOnContactUnitStatusStrategy(state, hbox, boss.getBodyData(), 
						new Ablaze(state, fireDuration, boss.getBodyData(), boss.getBodyData(), fireDamage)));
				hbox.addStrategy(new HitboxDamageStandardStrategy(state, hbox, boss.getBodyData(), null, baseDamage, knockback, DamageTypes.RANGED));
				new ParticleEntity(state, hbox, Particle.FIRE, 3.0f, 0.0f, true, particleSyncType.CREATESYNC);
			}
		});
	}
	
	public static int moveToRandomCorner(PlayState state, Boss1 boss, int speed) {
		int rand = GameStateManager.generator.nextInt(4);
		switch(rand) {
		case 0:
			BossUtils.moveToDummy(state, boss, "0", speed);
			break;
		case 1:
			BossUtils.moveToDummy(state, boss, "2", speed);
			break;
		case 2:
			BossUtils.moveToDummy(state, boss, "6", speed);
			break;
		case 3:
			BossUtils.moveToDummy(state, boss, "8", speed);
			break;
		default:
		}
		return rand;
	}
	
	public static int moveToRandomWall(PlayState state, Boss1 boss, int speed) {
		int rand = GameStateManager.generator.nextInt(2);
		switch(rand) {
		case 0:
			BossUtils.moveToDummy(state, boss, "3", speed);
			break;
		case 1:
			BossUtils.moveToDummy(state, boss, "5", speed);
			break;
		}
		return rand;
	}
	
	public static int normalizeAngle(int angle)
	{
	    int newAngle = angle;
	    while (newAngle <= -180) newAngle += 360;
	    while (newAngle > 180) newAngle -= 360;
	    return newAngle;
	}
}
