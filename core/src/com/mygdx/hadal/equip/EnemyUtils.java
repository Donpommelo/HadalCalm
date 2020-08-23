package com.mygdx.hadal.equip;

import com.mygdx.hadal.schmucks.bodies.enemies.Turret;
import com.mygdx.hadal.schmucks.bodies.enemies.Turret.TurretState;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.strategies.HitboxStrategy;
import com.mygdx.hadal.strategies.hitbox.ContactUnitBurn;
import com.mygdx.hadal.strategies.hitbox.ContactUnitSound;
import com.mygdx.hadal.strategies.hitbox.ContactWallDie;
import com.mygdx.hadal.strategies.hitbox.ContactWallLoseDurability;
import com.mygdx.hadal.strategies.hitbox.ContactWallParticles;
import com.mygdx.hadal.strategies.hitbox.ControllerDefault;
import com.mygdx.hadal.strategies.hitbox.CreateParticles;
import com.mygdx.hadal.strategies.hitbox.CreateSound;
import com.mygdx.hadal.strategies.hitbox.DamageStandard;
import com.mygdx.hadal.strategies.hitbox.DamageStatic;
import com.mygdx.hadal.strategies.hitbox.DieSound;
import com.mygdx.hadal.strategies.hitbox.FixedToEntity;
import com.mygdx.hadal.strategies.hitbox.FixedToOrigin;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.ParticleColor;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.schmucks.MoveState;
import com.mygdx.hadal.schmucks.bodies.HadalEntity;
import com.mygdx.hadal.schmucks.bodies.enemies.Enemy;
import com.mygdx.hadal.schmucks.bodies.enemies.EnemyAction;
import com.mygdx.hadal.schmucks.bodies.enemies.EnemyCrawling;
import com.mygdx.hadal.schmucks.bodies.enemies.EnemyCrawling.CrawlingState;
import com.mygdx.hadal.schmucks.bodies.enemies.EnemyFloating;
import com.mygdx.hadal.schmucks.bodies.enemies.EnemyFloating.FloatingState;
import com.mygdx.hadal.schmucks.bodies.enemies.EnemySwimming;
import com.mygdx.hadal.schmucks.bodies.enemies.EnemySwimming.SwimmingState;
import com.mygdx.hadal.schmucks.bodies.enemies.EnemyType;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.bodies.hitboxes.RangedHitbox;
import com.mygdx.hadal.utils.Constants;

/**
 * This contains several statichelper methods for creating enemy attack patterns
 * @author Zachary Tu
 */
public class EnemyUtils {

	public static void moveToDummy(final PlayState state, final Enemy boss, final String dummyId, final int speed, float duration) {
		
		boss.getActions().add(new EnemyAction(boss, duration) {
			
			@Override
			public void execute() {
				Event dummy = state.getDummyPoint(dummyId);
				
				if (dummy != null) {
					enemy.setMovementTarget(dummy.getPixelPosition());
					enemy.setMoveSpeed(speed);
				}
			}
		});
	}
	
	public static void changeMoveState(final PlayState state, final Enemy boss, final MoveState moveState, float duration) {
		
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
				bossFloating.setTrackSpeed(speed);
			}
		});
	}
	
	public static void changeFloatingState(final EnemyFloating bossFloating, final FloatingState state, final float angle, float duration) {
		
		bossFloating.getActions().add(new EnemyAction(bossFloating, duration) {
			
			@Override
			public void execute() {
				bossFloating.setCurrentState(state);
				bossFloating.setAttackAngle(normalizeAngle((int) bossFloating.getAttackAngle()));
				switch (state) {
				case FREE:
					bossFloating.setDesiredAngle(angle);
					break;
				case SPINNING:
				case ROTATING:
					bossFloating.setSpinSpeed((int) angle);
					break;
				case TRACKING_PLAYER:
					break;
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
				bossFloating.setCurrentState(FloatingState.FREE);
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
				bossSwimming.setCurrentState(state);
				switch (state) {
				case CHASE:
					bossSwimming.setMoveSpeed(speed);
					break;
				case STILL:
					bossSwimming.setMoveSpeed(0);
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
				bossSwimming.setCurrentState(SwimmingState.CHASE);
				bossSwimming.setMinRange(minRange);
				bossSwimming.setMaxRange(maxRange);
				bossSwimming.setMoveSpeed(speed);
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
					break;
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
					type.generateEnemy(state, enemy.getPixelPosition(), Constants.ENEMY_HITBOX, extraField, null);
				}
			}
		});
	}
	
	public static void moveToPlayer(final PlayState state, Enemy boss, final HadalEntity target, final int moveSpeed, final float duration) {
		
		boss.getActions().add(new EnemyAction(boss, duration) {
			
			@Override
			public void execute() {
				
				if (target == null) {
					return;
				}
				
				Vector2 dist = target.getPixelPosition().sub(enemy.getPixelPosition());
				enemy.setLinearVelocity(dist.nor().scl(moveSpeed));
			}
		});
	}
	
	public static void trackPlayerXY(final PlayState state, Enemy boss, final HadalEntity target, final int moveSpeed, final float duration, final boolean x) {
		
		boss.getActions().add(new EnemyAction(boss, duration) {
			
			@Override
			public void execute() {
				
				if (target == null) { return; }
				
				enemy.setMovementTarget(null);
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
		meleeAttackContinuous(state, boss, damage, attackInterval, knockback, duration, false);
	}
	
	public static void meleeAttackContinuous(final PlayState state, Enemy boss, final float damage, final float attackInterval, final float knockback, final float duration, boolean permanent) {
		
		boss.getActions().add(new EnemyAction(boss, 0) {
			
			@Override
			public void execute() {
				
				Hitbox hbox = new Hitbox(state, enemy.getPixelPosition(), enemy.getHboxSize(), duration, enemy.getLinearVelocity(), enemy.getHitboxfilter(), true, true, enemy, Sprite.NOTHING);
				hbox.makeUnreflectable();
				
				if (!permanent) {
					hbox.addStrategy(new ControllerDefault(state, hbox, enemy.getBodyData()));
				}
				
				hbox.addStrategy(new DamageStatic(state, hbox, enemy.getBodyData(), damage, knockback, DamageTypes.MELEE));
				hbox.addStrategy(new FixedToEntity(state, hbox, enemy.getBodyData(), new Vector2(), new Vector2(), true));
				hbox.addStrategy(new ContactUnitSound(state, hbox, enemy.getBodyData(), SoundEffect.DAMAGE3, 0.6f, true));
				hbox.addStrategy((new HitboxStrategy(state, hbox, enemy.getBodyData()) {
				
					private float controllerCount = 0;
				
					@Override
					public void controller(float delta) {
						
						controllerCount += delta;
						
						while (controllerCount >= attackInterval) {
							controllerCount -= attackInterval;
							
							Hitbox pulse = new Hitbox(state, hbox.getPixelPosition(), enemy.getHboxSize(), attackInterval, new Vector2(0, 0), enemy.getHitboxfilter(), true, true, enemy, Sprite.NOTHING);
							pulse.setSyncDefault(false);
							pulse.makeUnreflectable();
							pulse.addStrategy(new ControllerDefault(state, pulse, enemy.getBodyData()));
							pulse.addStrategy(new DamageStatic(state, pulse, enemy.getBodyData(), damage, knockback, DamageTypes.MELEE));
							pulse.addStrategy(new FixedToEntity(state, pulse, enemy.getBodyData(), new Vector2(), new Vector2(), true));
						}
					}
				}));
			}
		});
	}

	public static void windupParticles(final PlayState state, Enemy boss, final float duration, Particle particle, float size) {
		windupParticles(state, boss, duration, particle, ParticleColor.NOTHING, size);
	}
	
	public static void windupParticles(final PlayState state, Enemy boss, final float duration, Particle particle, ParticleColor color, float size) {
		
		boss.getActions().add(new EnemyAction(boss, duration) {
			
			@Override
			public void execute() {
				Vector2 startVelo = new Vector2(0, 1).setAngle(enemy.getAttackAngle());
				Hitbox hbox = new Hitbox(state, enemy.getPixelPosition(), enemy.getHboxSize(), duration, startVelo, enemy.getHitboxfilter(), true, true, enemy, Sprite.NOTHING);

				hbox.addStrategy(new ControllerDefault(state, hbox, enemy.getBodyData()));
				hbox.addStrategy(new FixedToOrigin(state, hbox, enemy, false));
				hbox.addStrategy(new CreateParticles(state, hbox, enemy.getBodyData(), particle, 0.0f, fireLinger).setParticleColor(color).setParticleSize(size));
			}
		});
	}
	
	public static void createSound(final PlayState state, Enemy boss, final float duration, float volume, SoundEffect sound) {
		
		boss.getActions().add(new EnemyAction(boss, duration) {
			
			@Override
			public void execute() {
				sound.playUniversal(state, boss.getPixelPosition(), volume, false);
			}
		});
	}
	
	public static void createSoundEntity(final PlayState state, Enemy boss, final float duration, float soundDuration, float volume, float pitch, SoundEffect sound, boolean looped) {
		
		boss.getActions().add(new EnemyAction(boss, duration) {
			
			@Override
			public void execute() {
				Vector2 startVelo = new Vector2(0, 1).setAngle(enemy.getAttackAngle());
				Hitbox hbox = new Hitbox(state, enemy.getPixelPosition(), enemy.getHboxSize(), soundDuration, startVelo, enemy.getHitboxfilter(), true, true, enemy, Sprite.NOTHING);

				hbox.addStrategy(new ControllerDefault(state, hbox, enemy.getBodyData()));
				hbox.addStrategy(new FixedToOrigin(state, hbox, enemy, false));
				hbox.addStrategy(new CreateSound(state, hbox, enemy.getBodyData(), sound, volume, looped).setPitch(pitch));
			}
		});
	}

	public static void projectile(final PlayState state, Enemy boss, final float baseDamage, final float projSpeed, final float knockback, final int size, final float lifespan, final float duration, Particle particle) {
		
		boss.getActions().add(new EnemyAction(boss, duration) {
			
			@Override
			public void execute() {
				Vector2 startVelo = new Vector2(projSpeed, projSpeed).setAngle(enemy.getAttackAngle());
				RangedHitbox hbox = new RangedHitbox(state, enemy.getProjectileOrigin(startVelo, size), new Vector2(size, size), lifespan, startVelo, enemy.getHitboxfilter(), false, true, enemy, Sprite.NOTHING);
				
				hbox.addStrategy(new ControllerDefault(state, hbox, enemy.getBodyData()));
				hbox.addStrategy(new DamageStandard(state, hbox, enemy.getBodyData(), baseDamage, knockback, DamageTypes.RANGED));
				hbox.addStrategy(new CreateParticles(state, hbox, enemy.getBodyData(), particle, 0.0f, fireLinger));
			}
		});
	}
	
	private static final float fireLinger = 3.0f;
	private static final float laserLinger = 0.01f;
	public static void fireball(final PlayState state, Enemy boss, final float baseDamage, final float fireDamage, final float projSpeed, final float knockback, final int size,
			final float lifespan, final float fireDuration, final float duration, Particle particle) {
		
		boss.getActions().add(new EnemyAction(boss, duration) {
			
			@Override
			public void execute() {
				Vector2 startVelo = new Vector2(projSpeed, projSpeed).setAngle(enemy.getAttackAngle());
				RangedHitbox hbox = new RangedHitbox(state, enemy.getProjectileOrigin(startVelo, size), new Vector2(size, size), lifespan, startVelo, enemy.getHitboxfilter(), false, true, enemy, Sprite.NOTHING);
				
				hbox.addStrategy(new ControllerDefault(state, hbox, enemy.getBodyData()));
				hbox.addStrategy(new ContactUnitBurn(state, hbox, enemy.getBodyData(), fireDuration, fireDamage));
				hbox.addStrategy(new DamageStandard(state, hbox, enemy.getBodyData(), baseDamage, knockback, DamageTypes.RANGED, DamageTypes.FIRE));
				hbox.addStrategy(new CreateParticles(state, hbox, enemy.getBodyData(), particle, 0.0f, fireLinger));
			}
		});
	}
	
	public static void fireLaser(final PlayState state, Enemy boss, final float baseDamage, final float projSpeed, final float knockback, final int size, final float lifespan, final float duration, final Particle particle) {
		
		boss.getActions().add(new EnemyAction(boss, duration) {
			
			@Override
			public void execute() {
				Vector2 startVelo = new Vector2(projSpeed, projSpeed).setAngle(enemy.getAttackAngle());
				RangedHitbox hbox = new RangedHitbox(state, enemy.getProjectileOrigin(startVelo, size), new Vector2(size, size), lifespan, startVelo, enemy.getHitboxfilter(), true, true, enemy, Sprite.NOTHING);
				
				hbox.addStrategy(new ControllerDefault(state, hbox, enemy.getBodyData()));
				hbox.addStrategy(new DamageStandard(state, hbox, enemy.getBodyData(), baseDamage, knockback, DamageTypes.RANGED));
				hbox.addStrategy(new ContactWallDie(state, hbox, enemy.getBodyData()));
				hbox.addStrategy(new CreateParticles(state, hbox, enemy.getBodyData(), particle, 0.0f, laserLinger));
			}
		});
	}
	
	public static void shootBullet(final PlayState state, Enemy boss, final float baseDamage, final float projSpeed, final float knockback, final int size, final float lifespan, final float duration) {
		boss.getActions().add(new EnemyAction(boss, duration) {
			
			@Override
			public void execute() {
				Vector2 startVelo = new Vector2(projSpeed, projSpeed).setAngle(enemy.getAttackAngle());
				Hitbox hbox = new Hitbox(state, enemy.getProjectileOrigin(startVelo, size), new Vector2(size, size), lifespan, startVelo, enemy.getHitboxfilter(), true, true, enemy, Sprite.ORB_RED);
				
				hbox.addStrategy(new ControllerDefault(state, hbox, enemy.getBodyData()));
				hbox.addStrategy(new DamageStandard(state, hbox, enemy.getBodyData(), baseDamage, knockback, DamageTypes.RANGED));
				hbox.addStrategy(new ContactWallDie(state, hbox, enemy.getBodyData()));
				hbox.addStrategy(new ContactUnitSound(state, hbox, enemy.getBodyData(), SoundEffect.DAMAGE3, 0.6f, true));
			}
		});
	}
	
	private final static Sprite[] debrisSprites = {Sprite.SCRAP_A, Sprite.SCRAP_B, Sprite.SCRAP_C, Sprite.SCRAP_D};
	public static void fallingDebris(final PlayState state, Enemy boss, final float baseDamage, final int size, final float knockback, final float lifespan, final float duration) {
		
		boss.getSecondaryActions().add(new EnemyAction(boss, duration) {
			
			@Override
			public void execute() {
				
				Event ceiling = state.getDummyPoint("ceiling");
				
				if (ceiling != null) {
					
					int randomIndex = GameStateManager.generator.nextInt(debrisSprites.length);
					Sprite projSprite = debrisSprites[randomIndex];
					Hitbox hbox = new Hitbox(state, new Vector2(ceiling.getPixelPosition()).add(new Vector2((GameStateManager.generator.nextFloat() -  0.5f) * ceiling.getSize().x, 0)),
							new Vector2(size, size), lifespan, new Vector2(), enemy.getHitboxfilter(), true, true, enemy, projSprite);
					hbox.setDurability(2);
					hbox.setGravity(1.0f);
					
					hbox.addStrategy(new ControllerDefault(state, hbox, enemy.getBodyData()));
					hbox.addStrategy(new DamageStandard(state, hbox, enemy.getBodyData(), baseDamage, knockback, DamageTypes.RANGED));
					hbox.addStrategy(new ContactWallLoseDurability(state, hbox, enemy.getBodyData()));
					hbox.addStrategy(new ContactWallParticles(state, hbox, enemy.getBodyData(), Particle.SPARKS));
					hbox.addStrategy(new ContactUnitSound(state, hbox, enemy.getBodyData(), SoundEffect.DAMAGE3, 0.6f, true));
					hbox.addStrategy(new DieSound(state, hbox, enemy.getBodyData(), SoundEffect.WALL_HIT1, 0.4f));
				}
			}
		});
	}
	
	public static void callMinion(final PlayState state, Enemy boss, final float duration, final EnemyType type, final float extraField) {
		boss.getSecondaryActions().add(new EnemyAction(boss, duration) {
			
			@Override
			public void execute() {
				
				Event ceiling = state.getDummyPoint("ceiling");
				
				if (ceiling != null) {
					
					type.generateEnemy(state, new Vector2(ceiling.getPixelPosition()).add(new Vector2((GameStateManager.generator.nextFloat() -  0.5f) * ceiling.getSize().x, 0)),
							enemy.getHitboxfilter(), extraField, null);
				}
			}
		});
	}
	
	public static int moveToRandomCorner(PlayState state, Enemy boss, int speed, float duration) {
		int rand = GameStateManager.generator.nextInt(4);
		switch(rand) {
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
		int rand = GameStateManager.generator.nextInt(2);
		switch(rand) {
		case 0:
			EnemyUtils.moveToDummy(state, boss, "3", speed, duration);
			break;
		case 1:
			EnemyUtils.moveToDummy(state, boss, "5", speed, duration);
			break;
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
		
		if (ceiling != null) {
			return ceiling.getPixelPosition().y;
		} else {
			return 0.0f;
		}
	}
	
	public static float floorHeight(PlayState state) {
		
		Event floor = state.getDummyPoint("floor");
		
		if (floor != null) {
			return floor.getPixelPosition().y;
		} else {
			return 0.0f;
		}
	}
	
	public static float getLeftSide(PlayState state) {
		Event floor = state.getDummyPoint("floor");
		
		if (floor != null) {
			return floor.getPixelPosition().x - floor.getSize().x / 2;
		} else {
			return 0.0f;
		}
	}
	
	public static float getRightSide(PlayState state) {
		Event floor = state.getDummyPoint("floor");
		
		if (floor != null) {
			return floor.getPixelPosition().x + floor.getSize().x / 2;
		} else {
			return 0.0f;
		}
	}
}
