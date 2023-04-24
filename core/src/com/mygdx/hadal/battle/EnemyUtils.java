package com.mygdx.hadal.battle;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.constants.Constants;
import com.mygdx.hadal.constants.MoveState;
import com.mygdx.hadal.effects.HadalColor;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.schmucks.entities.HadalEntity;
import com.mygdx.hadal.schmucks.entities.enemies.*;
import com.mygdx.hadal.schmucks.entities.enemies.EnemyCrawling.CrawlingState;
import com.mygdx.hadal.schmucks.entities.enemies.Turret.TurretState;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.entities.hitboxes.RangedHitbox;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.enemy.MovementFloat.FloatingState;
import com.mygdx.hadal.strategies.enemy.MovementSwim.SwimmingState;
import com.mygdx.hadal.strategies.hitbox.*;

/**
 * This contains several static helper methods for creating enemy attack patterns
 * @author Cementine Choldous
 */
public class EnemyUtils {

	public static void moveToDummy(final PlayState state, final Enemy boss, final String dummyId, final int speed, float duration) {
		
		boss.getActions().add(new EnemyAction(boss, duration) {
			
			@Override
			public void execute() {
				Event dummy = state.getDummyPoint(dummyId);

				if (null != dummy) {
					enemy.setMovementTarget(dummy, speed);
				}
			}
		});
	}
	
	public static void changeMoveState(final Enemy boss, final MoveState moveState, float duration) {
		
		boss.getActions().add(new EnemyAction(boss, duration) {
			
			@Override
			public void execute() {
				boss.setMoveState(moveState);
			}
		});
	}
	
	public static void changeFloatingTrackSpeed(final EnemyFloating bossFloating, final float speed, float duration) {
		
		bossFloating.getActions().add(new EnemyAction(bossFloating, duration) {
			
			@Override
			public void execute() {
				bossFloating.getFloatStrategy().setTrackSpeed(speed);
			}
		});
	}
	
	public static void changeFloatingState(final EnemyFloating bossFloating, final FloatingState state,
										   final float angle, float duration) {
		
		bossFloating.getActions().add(new EnemyAction(bossFloating, duration) {
			
			@Override
			public void execute() {
				bossFloating.getFloatStrategy().setCurrentState(state);
				bossFloating.setAttackAngle(normalizeAngle((int) bossFloating.getAttackAngle()));
				switch (state) {
				case FREE:
					bossFloating.setDesiredAngle(angle);
					break;
				case SPINNING:
				case ROTATING:
					bossFloating.getFloatStrategy().setSpinSpeed((int) angle);
					break;
				case TRACKING_PLAYER:
				default:
					break;
				}
			}
		});
	}
	
	public static void changeFloatingFreeAngle(final EnemyFloating bossFloating, final float angle, float duration) {
		
		bossFloating.getActions().add(new EnemyAction(bossFloating, duration) {
			
			@Override
			public void execute() {
				bossFloating.getFloatStrategy().setCurrentState(FloatingState.FREE);
				bossFloating.setAttackAngle(normalizeAngle((int) bossFloating.getAttackAngle()));
				bossFloating.setDesiredAngle(bossFloating.getAttackAngle() + angle);
			}
		});
	}
	
	public static void changeCrawlingState(final EnemyCrawling bossCrawling, final CrawlingState state, final float speed, float duration) {
		
		bossCrawling.getActions().add(new EnemyAction(bossCrawling, duration) {
			
			@Override
			public void execute() {
				bossCrawling.setCurrentState(state);
				switch (state) {
				case BACK_FORTH:
				case AVOID_PITS:
				case CHASE:
					bossCrawling.setMoveSpeed(speed);
					break;
				case STILL:
					bossCrawling.setMoveSpeed(0);
					break;
				default:
					break;
				}
			}
		});
	}
	
	public static void setCrawlingChaseState(final EnemyCrawling bossCrawling, final float speed, final float minRange, final float maxRange, float duration) {
		
		bossCrawling.getActions().add(new EnemyAction(bossCrawling, duration) {
			
			@Override
			public void execute() {
				bossCrawling.setCurrentState(CrawlingState.CHASE);
				bossCrawling.setMinRange(minRange);
				bossCrawling.setMaxRange(maxRange);
				bossCrawling.setMoveSpeed(speed);
			}
		});
	}
	
	public static void changeSwimmingState(final EnemySwimming bossSwimming, final SwimmingState state, final float speed, float duration) {
		
		bossSwimming.getActions().add(new EnemyAction(bossSwimming, duration) {
			
			@Override
			public void execute() {
				bossSwimming.getSwimStrategy().setCurrentState(state);
				switch (state) {
				case CHASE:
					bossSwimming.getSwimStrategy().setMoveSpeed(speed);
					break;
				case STILL:
					bossSwimming.getSwimStrategy().setMoveSpeed(0);
					break;
				default:
					break;
				}
			}
		});
	}

	public static void setSwimmingChaseState(final EnemySwimming bossSwimming, final float speed, final float minRange, final float maxRange, float duration) {
		
		bossSwimming.getActions().add(new EnemyAction(bossSwimming, duration) {
			
			@Override
			public void execute() {
				bossSwimming.getSwimStrategy().setCurrentState(SwimmingState.CHASE);
				bossSwimming.getSwimStrategy().setMinRange(minRange);
				bossSwimming.getSwimStrategy().setMaxRange(maxRange);
				bossSwimming.getSwimStrategy().setMoveSpeed(speed);
			}
		});
	}

	public static void changeTurretState(final Turret turret, final TurretState state, final float angle, float duration) {
		
		turret.getActions().add(new EnemyAction(turret, duration) {
			
			@Override
			public void execute() {
				turret.setCurrentState(state);
				turret.setAttackAngle(normalizeAngle((int) turret.getAttackAngle()));
				switch (state) {
				case FREE:
					turret.setDesiredAngle(turret.getAttackAngle() + angle);
					break;
				case TRACKING:
				default:
					break;
				}
			}
		});
	}
	
	public static void spawnAdds(final PlayState state, Enemy boss, final EnemyType type, final int amount, final float extraField, float duration) {
		
		boss.getActions().add(new EnemyAction(boss, duration) {
			
			@Override
			public void execute() {
				
				for (int i = 0; i < amount; i++) {
					type.generateEnemy(state, enemy.getPixelPosition(), Constants.ENEMY_HITBOX, extraField);
				}
			}
		});
	}
	
	public static void moveToPlayer(Enemy boss, final HadalEntity target, final int moveSpeed, final float duration) {
		
		boss.getActions().add(new EnemyAction(boss, duration) {
			
			@Override
			public void execute() {
				
				if (null == target) { return; }
				
				Vector2 dist = target.getPixelPosition().sub(enemy.getPixelPosition());
				enemy.setLinearVelocity(dist.nor().scl(moveSpeed));
			}
		});
	}
	
	public static void trackPlayerXY(Enemy boss, final HadalEntity target, final int moveSpeed, final float duration, final boolean x) {
		
		boss.getActions().add(new EnemyAction(boss, duration) {
			
			@Override
			public void execute() {
				
				if (null == target) { return; }
				
				enemy.setMovementTarget(null, moveSpeed);
				Vector2 dist = target.getPixelPosition().sub(enemy.getPixelPosition());
				if (x) {
					enemy.setLinearVelocity(new Vector2(dist.nor().scl(moveSpeed).x, 0));
				} else {
					enemy.setLinearVelocity(new Vector2(0, dist.nor().scl(moveSpeed).y));
				}
			}
		});
	}
	
	public static void meleeAttackContinuous(final PlayState state, Enemy boss, final float damage, final float attackInterval, final float knockback, final float duration) {
		
		boss.getActions().add(new EnemyAction(boss, 0) {
			
			@Override
			public void execute() {

				//we intentionally send hbox size here instead of position, b/c its attached to the user
				SyncedAttack.CONTACT_DAMAGE.initiateSyncedAttackSingle(state, enemy, enemy.getHboxSize(), new Vector2(),
						duration, damage, knockback, attackInterval);
			}
		});
	}

	public static void windupParticles(final PlayState state, Enemy boss, final float duration, Particle particle, float size) {
		windupParticles(state, boss, duration, particle, HadalColor.NOTHING, size);
	}
	
	public static void windupParticles(final PlayState state, Enemy boss, final float duration, Particle particle, HadalColor color, float size) {
		
		boss.getActions().add(new EnemyAction(boss, duration) {
			
			@Override
			public void execute() {
				Vector2 startVelo = new Vector2(0, 1).setAngleDeg(enemy.getAttackAngle());
				Hitbox hbox = new Hitbox(state, enemy.getPixelPosition(), enemy.getHboxSize(), duration, startVelo, enemy.getHitboxFilter(), true, true, enemy, Sprite.NOTHING);
				hbox.setSyncDefault(false);
				hbox.setSyncInstant(true);
				
				hbox.addStrategy(new ControllerDefault(state, hbox, enemy.getBodyData()));
				hbox.addStrategy(new FixedToOrigin(state, hbox, enemy, false));
				hbox.addStrategy(new CreateParticles(state, hbox, enemy.getBodyData(), particle, 0.0f, fireLinger).setParticleColor(color).setParticleSize(size));
			}
		});
	}
	
	public static void createSoundEntity(final PlayState state, Enemy boss, final float duration, float soundDuration, float volume, float pitch, SoundEffect sound, boolean looped) {
		
		boss.getActions().add(new EnemyAction(boss, duration) {
			
			@Override
			public void execute() {
				Hitbox hbox = new Hitbox(state, enemy.getPixelPosition(), enemy.getHboxSize(), soundDuration, new Vector2(), enemy.getHitboxFilter(), true, true, enemy, Sprite.NOTHING);
				hbox.setSynced(true);
				hbox.setSyncedDelete(true);

				hbox.addStrategy(new ControllerDefault(state, hbox, enemy.getBodyData()));
				hbox.addStrategy(new FixedToOrigin(state, hbox, enemy, false));
				hbox.addStrategy(new CreateSound(state, hbox, enemy.getBodyData(), sound, volume, looped).setPitch(pitch));
			}
		});
	}

	private static final float fireLinger = 1.0f;
	private static final float laserLinger = 0.01f;
	public static void fireball(final PlayState state, Enemy boss, final float projSpeed, final float duration, final float extraField) {
		
		boss.getActions().add(new EnemyAction(boss, duration) {
			
			@Override
			public void execute() {
				Vector2 startVelo = new Vector2(projSpeed, projSpeed).setAngleDeg(enemy.getAttackAngle());
				SyncedAttack.BOSS_FIRE_BREATH.initiateSyncedAttackSingle(state, enemy, enemy.getPixelPosition(), startVelo, extraField);
			}
		});
	}
	
	public static void fireLaser(final PlayState state, Enemy boss, final float baseDamage, final float projSpeed, final float knockback, final int size, final float lifespan, final float duration, final Particle particle) {
		
		boss.getActions().add(new EnemyAction(boss, duration) {
			
			@Override
			public void execute() {
				Vector2 startVelo = new Vector2(projSpeed, projSpeed).setAngleDeg(enemy.getAttackAngle());
				RangedHitbox hbox = new RangedHitbox(state, enemy.getProjectileOrigin(startVelo, size), new Vector2(size, size), lifespan, startVelo, enemy.getHitboxFilter(), true, true, enemy, Sprite.NOTHING);
				
				hbox.addStrategy(new ControllerDefault(state, hbox, enemy.getBodyData()));
				hbox.addStrategy(new DamageStandard(state, hbox, enemy.getBodyData(), baseDamage, knockback,
						DamageSource.ENEMY_ATTACK, DamageTag.RANGED));
				hbox.addStrategy(new ContactWallDie(state, hbox, enemy.getBodyData()));
				hbox.addStrategy(new CreateParticles(state, hbox, enemy.getBodyData(), particle, 0.0f, laserLinger));
			}
		});
	}
	
	public static void shootBullet(final PlayState state, Enemy boss, final float baseDamage, final float projSpeed, final float knockback, final int size, final float lifespan, final float duration) {
		boss.getActions().add(new EnemyAction(boss, duration) {
			
			@Override
			public void execute() {
				Vector2 startVelo = new Vector2(projSpeed, projSpeed).setAngleDeg(enemy.getAttackAngle());
				Hitbox hbox = new Hitbox(state, enemy.getProjectileOrigin(startVelo, size), new Vector2(size, size), lifespan, startVelo, enemy.getHitboxFilter(), true, true, enemy, Sprite.ORB_RED);
				
				hbox.addStrategy(new ControllerDefault(state, hbox, enemy.getBodyData()));
				hbox.addStrategy(new DamageStandard(state, hbox, enemy.getBodyData(), baseDamage, knockback,
						DamageSource.ENEMY_ATTACK, DamageTag.RANGED));
				hbox.addStrategy(new ContactWallDie(state, hbox, enemy.getBodyData()));
				hbox.addStrategy(new ContactUnitSound(state, hbox, enemy.getBodyData(), SoundEffect.DAMAGE3, 0.6f, true));
			}
		});
	}
	
	public static void fallingDebris(final PlayState state, Enemy boss, final float duration) {
		
		boss.getSecondaryActions().add(new EnemyAction(boss, duration) {
			
			@Override
			public void execute() {
				Event ceiling = state.getDummyPoint("ceiling");
				if (null != ceiling) {
					SyncedAttack.BOSS_FALLING_DEBRIS.initiateSyncedAttackSingle(state, enemy,
							new Vector2(ceiling.getPixelPosition()).add(new Vector2((MathUtils.random() -  0.5f) * ceiling.getSize().x, 0)),
							new Vector2());
				}
			}
		});
	}
	
	public static void callMinion(final PlayState state, Enemy boss, final float duration, final EnemyType type, final float extraField) {
		boss.getSecondaryActions().add(new EnemyAction(boss, duration) {
			
			@Override
			public void execute() {
				Event ceiling = state.getDummyPoint("ceiling");
				if (null != ceiling) {
					type.generateEnemy(state, new Vector2(ceiling.getPixelPosition()).add(new Vector2((MathUtils.random() -  0.5f) * ceiling.getSize().x, 0)),
							enemy.getHitboxFilter(), extraField);
				}
			}
		});
	}
	
	public static int moveToRandomCorner(PlayState state, Enemy boss, int speed, float duration) {
		int rand = MathUtils.random(3);
		switch (rand) {
		case 0:
			EnemyUtils.moveToDummy(state, boss, "0", speed, duration);
			break;
		case 1:
			EnemyUtils.moveToDummy(state, boss, "2", speed, duration);
			break;
		case 2:
			EnemyUtils.moveToDummy(state, boss, "6", speed, duration);
			break;
		case 3:
			EnemyUtils.moveToDummy(state, boss, "8", speed, duration);
			break;
		default:
		}
		return rand;
	}
	
	public static int moveToRandomWall(PlayState state, Enemy boss, int speed, float duration) {
		int rand = MathUtils.random(1);
		switch (rand) {
			case 0 -> EnemyUtils.moveToDummy(state, boss, "3", speed, duration);
			case 1 -> EnemyUtils.moveToDummy(state, boss, "5", speed, duration);
		}
		return rand;
	}
	
	public static void stopStill(Enemy boss, final float duration) {
		boss.getActions().add(new EnemyAction(boss, duration) {
			
			@Override
			public void execute() {
				enemy.setLinearVelocity(0, 0);
			}
		});
	}
	
	public static int normalizeAngle(int angle)
	{
	    int newAngle = angle;
	    while (newAngle <= -180) newAngle += 360;
	    while (newAngle > 180) newAngle -= 360;
	    return newAngle;
	}
	
	public static float ceilingHeight(PlayState state) {
		Event ceiling = state.getDummyPoint("ceiling");
		if (null != ceiling) {
			return ceiling.getPixelPosition().y;
		} else {
			return 0.0f;
		}
	}
	
	public static float floorHeight(PlayState state) {
		Event floor = state.getDummyPoint("floor");
		if (null != floor) {
			return floor.getPixelPosition().y;
		} else {
			return 0.0f;
		}
	}
	
	public static float getLeftSide(PlayState state) {
		Event floor = state.getDummyPoint("floor");
		if (null != floor) {
			return floor.getPixelPosition().x - floor.getSize().x / 2;
		} else {
			return 0.0f;
		}
	}
	
	public static float getRightSide(PlayState state) {
		Event floor = state.getDummyPoint("floor");
		if (null != floor) {
			return floor.getPixelPosition().x + floor.getSize().x / 2;
		} else {
			return 0.0f;
		}
	}
}
