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
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.event.Poison;
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.schmucks.bodies.HadalEntity;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity.particleSyncType;
import com.mygdx.hadal.schmucks.bodies.enemies.Boss;
import com.mygdx.hadal.schmucks.bodies.enemies.BossAction;
import com.mygdx.hadal.schmucks.bodies.enemies.BossFloating;
import com.mygdx.hadal.schmucks.bodies.enemies.BossFloating.BossState;
import com.mygdx.hadal.schmucks.bodies.enemies.Enemy.enemyType;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.bodies.hitboxes.HitboxSprite;
import com.mygdx.hadal.schmucks.bodies.hitboxes.MeleeHitbox;
import com.mygdx.hadal.schmucks.bodies.hitboxes.RangedHitbox;
import com.mygdx.hadal.schmucks.strategies.HitboxDamageStandardStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxDefaultStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxOnContactUnitStatusStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxOnContactWallDieStrategy;
import com.mygdx.hadal.utils.Constants;

public class BossUtils {

	public static void moveToDummy(final PlayState state, final Boss boss, final String dummyId, final int speed) {
		
		boss.getActions().add(new BossAction(boss, speed) {
			
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
	
	public static void changeTrackingState(final BossFloating bossFloating, final BossState state, final float angle, float duration) {
		
		bossFloating.getActions().add(new BossAction(bossFloating, duration) {
			
			@Override
			public void execute() {
				bossFloating.setCurrentState(state);
				bossFloating.setAngle(normalizeAngle((int) bossFloating.getAngle()));
				switch (state) {
				case FREE:
					bossFloating.setDesiredAngle(angle);
					break;
				case SPINNING:
					bossFloating.setSpinSpeed((int)angle);
					break;
				case LOCKED:
					bossFloating.setAngle(angle);
					break;
				case TRACKING_PLAYER:
					break;
				default:
					break;
				
				}
			}
		});
	}
	
	public static void spawnAdds(final PlayState state, Boss boss, final enemyType type, final int amount, float duration) {
		
		boss.getActions().add(new BossAction(boss, duration) {
			
			@Override
			public void execute() {
				
				for (int i = 0; i < amount; i++) {
					switch (type) {
					case SCISSORFISH:
						new Scissorfish(state, (int)(boss.getPosition().x * PPM), (int)(boss.getPosition().y * PPM), Constants.ENEMY_HITBOX, null);
						break;
					case SPITTLEFISH:
						new Spittlefish(state, (int)(boss.getPosition().x * PPM), (int)(boss.getPosition().y * PPM), Constants.ENEMY_HITBOX, null);
						break;
					case TORPEDOFISH:
						new Torpedofish(state, (int)(boss.getPosition().x * PPM), (int)(boss.getPosition().y * PPM), Constants.ENEMY_HITBOX, null);
						break;
					default:
						break;
					}
				}
			}
		});
	}
	
	public static void moveToPlayer(final PlayState state, Boss boss, final HadalEntity target, final int moveSpeed, final float duration) {
		
		boss.getActions().add(new BossAction(boss, duration) {
			
			@Override
			public void execute() {
				Vector2 dist = target.getPosition().sub(boss.getPosition());
				boss.setLinearVelocity(dist.nor().scl(moveSpeed));
			}
		});
	}
	
	public static void trackPlayerXY(final PlayState state, Boss boss, final HadalEntity target, final int moveSpeed, final float duration, final boolean x) {
		
		boss.getActions().add(new BossAction(boss, duration) {
			
			@Override
			public void execute() {
				boss.setMovementTarget(null);
				Vector2 dist = target.getPosition().sub(boss.getPosition());
				if (x) {
					boss.setLinearVelocity(new Vector2(dist.nor().scl(moveSpeed).x, 0));
				} else {
					boss.setLinearVelocity(new Vector2(0, dist.nor().scl(moveSpeed).y));
				}
			}
		});
	}
	
	public static void meleeAttack(final PlayState state, Boss boss, final float damage, final float knockback, final HadalEntity target, final float duration) {
		
		boss.getActions().add(new BossAction(boss, 0) {
			
			@Override
			public void execute() {
				
				Hitbox hbox = new MeleeHitbox(state, boss.getPosition().x * PPM, boss.getPosition().y * PPM, (int)boss.getHeight() + 10, (int)boss.getWidth() + 10, duration, duration, 
						new Vector2(), new Vector2(), true, boss.getHitboxfilter(), boss) {
					
					/**
					 * This just makes sure the melee hitbox tracks the position of the user.
					 */
					@Override
					public void controller(float delta) {
								
						super.controller(delta);
						
						//Melee hboxes should not persist after owner's disposal. Otherwise, track location
						if (!creator.isAlive()) {
							queueDeletion();
						} else {
							Vector2 hbLocation = creator.getPosition().add(center);
							setTransform(hbLocation, (float) (boss.getBody().getAngle() +  Math.PI / 2));
						}
						
						lifeSpan -= delta;
						if (lifeSpan <= 0) {
							queueDeletion();
						}
					}
				};
				
				hbox.addStrategy(new HitboxDefaultStrategy(state, hbox, boss.getBodyData()));
				hbox.addStrategy(new HitboxDamageStandardStrategy(state, hbox, boss.getBodyData(), null, damage, knockback, DamageTypes.MELEE));	
			}
		});
	}

	public static void fireball(final PlayState state, Boss boss, final float baseDamage, final float fireDamage, final float projSpeed, final float knockback, final int size, final float gravity, 
			final float lifespan, final float fireDuration, final float duration) {
		
		boss.getActions().add(new BossAction(boss, duration) {
			
			@Override
			public void execute() {
				
				RangedHitbox hbox = new RangedHitbox(state, boss.getPosition().x * PPM, boss.getPosition().y * PPM, size, size, gravity, lifespan, 1, 0, new Vector2(projSpeed, projSpeed).setAngle(boss.getAttackAngle()),
						boss.getHitboxfilter(), false, true, boss);
				
				hbox.addStrategy(new HitboxDefaultStrategy(state, hbox, boss.getBodyData()));
				hbox.addStrategy(new HitboxOnContactUnitStatusStrategy(state, hbox, boss.getBodyData(), 
						new Ablaze(state, fireDuration, boss.getBodyData(), boss.getBodyData(), fireDamage)));
				hbox.addStrategy(new HitboxDamageStandardStrategy(state, hbox, boss.getBodyData(), null, baseDamage, knockback, DamageTypes.RANGED));
				new ParticleEntity(state, hbox, Particle.FIRE, 3.0f, 0.0f, true, particleSyncType.CREATESYNC);
			}
		});
	}
	
	public static void fireLaser(final PlayState state, Boss boss, final float baseDamage, final float projSpeed, final float knockback, final int size, final float lifespan, final float duration, final Particle particle) {
		
		boss.getActions().add(new BossAction(boss, duration) {
			
			@Override
			public void execute() {
				
				RangedHitbox hbox = new RangedHitbox(state, boss.getPosition().x * PPM, boss.getPosition().y * PPM, size, size, 0, lifespan, 1, 0, new Vector2(projSpeed, projSpeed).setAngle(boss.getAttackAngle()),
						boss.getHitboxfilter(), true, true, boss);
				
				hbox.addStrategy(new HitboxDefaultStrategy(state, hbox, boss.getBodyData()));
				hbox.addStrategy(new HitboxDamageStandardStrategy(state, hbox, boss.getBodyData(), null, baseDamage, knockback, DamageTypes.RANGED));
				hbox.addStrategy(new HitboxOnContactWallDieStrategy(state, hbox, boss.getBodyData()));
				new ParticleEntity(state, hbox, particle, 3.0f, 0.0f, true, particleSyncType.CREATESYNC);
			}
		});
	}
	
	public static void bouncingBall(final PlayState state, Boss boss, final float baseDamage, final float projSpeed, final float knockback, final int size, final float lifespan, final float duration) {
		boss.getActions().add(new BossAction(boss, duration) {
			
			@Override
			public void execute() {
				HitboxSprite hbox = new HitboxSprite(state, boss.getPosition().x * PPM, boss.getPosition().y * PPM, size, size, 10, lifespan, 1, 1.0f, new Vector2(projSpeed, projSpeed).setAngle(boss.getAttackAngle()),
						boss.getHitboxfilter(), false, true, boss, Sprite.ORB_RED);
				hbox.addStrategy(new HitboxDefaultStrategy(state, hbox, boss.getBodyData()));
				hbox.addStrategy(new HitboxDamageStandardStrategy(state, hbox, boss.getBodyData(), null, baseDamage, knockback, DamageTypes.RANGED));
				new ParticleEntity(state, hbox, Particle.FIRE, 3.0f, 0.0f, true, particleSyncType.CREATESYNC);
				hbox.setFriction(0);
			}
		});
	}
	
	public static void vengefulSpirit(final PlayState state, Boss boss, final float baseDamage, final float knockback, final float lifespan, final Vector2 pos, final float duration) {
		
		boss.getActions().add(new BossAction(boss, duration) {
			
			@Override
			public void execute() {
				WeaponUtils.releaseVengefulSpirits(state, lifespan, baseDamage, knockback, pos, boss.getBodyData(), boss.getHitboxfilter());
			}
		});
	}
	
	public static void createExplosion(final PlayState state, Boss boss, final float baseDamage, final float knockback, final int radius, final Vector2 pos, final float duration) {
		boss.getActions().add(new BossAction(boss, duration) {
			
			@Override
			public void execute() {
				WeaponUtils.createExplosion(state, pos.x, pos.y, boss, null, radius, baseDamage, knockback, boss.getHitboxfilter());
			}
		});
	}
	
	public static void createPoison(final PlayState state, Boss boss, final int width, final int height, final float damage, final float lifespan, final Vector2 pos, final float duration) {
		
		boss.getActions().add(new BossAction(boss, duration) {
			@Override
			public void execute() {
				new Poison(state, width, height, (int)pos.x, (int)pos.y, damage, lifespan, boss, true, boss.getHitboxfilter());
			}
		});
	}
	
	private final static Sprite[] debrisSprites = {Sprite.SCRAP_A, Sprite.SCRAP_B, Sprite.SCRAP_C, Sprite.SCRAP_D};
	public static void fallingDebris(final PlayState state, Boss boss, final float baseDamage, final int size, final float knockback, final float lifespan, final float duration) {
		
		boss.getActions().add(new BossAction(boss, duration) {
			
			@Override
			public void execute() {
				
				Event ceiling = state.getDummyPoint("ceiling");
				
				if (ceiling != null) {
					
					int randomIndex = GameStateManager.generator.nextInt(debrisSprites.length);
					Sprite projSprite = debrisSprites[randomIndex];
					HitboxSprite hbox = new HitboxSprite(state, ceiling.getPosition().x * PPM + (GameStateManager.generator.nextFloat() -  0.5f) * ceiling.getWidth(),
							ceiling.getPosition().y * PPM, size, size, 1, lifespan, 1, 0.0f, new Vector2(),
							boss.getHitboxfilter(), false, true, boss, projSprite);
					
					hbox.addStrategy(new HitboxDefaultStrategy(state, hbox, boss.getBodyData()));
					hbox.addStrategy(new HitboxDamageStandardStrategy(state, hbox, boss.getBodyData(), null, baseDamage, knockback, DamageTypes.RANGED));
					hbox.addStrategy(new HitboxOnContactWallDieStrategy(state, hbox, boss.getBodyData()));
				}
			}
		});
	}
	
	public static int moveToRandomCorner(PlayState state, Boss boss, int speed) {
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
	
	public static int moveToRandomWall(PlayState state, Boss boss, int speed) {
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
	
	public static void stopStill(Boss boss, final float duration) {
		boss.getActions().add(new BossAction(boss, duration) {
			
			@Override
			public void execute() {
				boss.setLinearVelocity(0, 0);
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
			return ceiling.getPosition().y * PPM;
		} else {
			return 0.0f;
		}
	}
	
	public static float floorHeight(PlayState state) {
		
		Event floor = state.getDummyPoint("floor");
		
		if (floor != null) {
			return floor.getPosition().y * PPM;
		} else {
			return 0.0f;
		}
	}
	
	public static float getLeftSide(PlayState state) {
		Event floor = state.getDummyPoint("floor");
		
		if (floor != null) {
			return floor.getPosition().x * PPM - floor.getWidth() / 2;
		} else {
			return 0.0f;
		}
	}
	
	public static float getRightSide(PlayState state) {
		Event floor = state.getDummyPoint("floor");
		
		if (floor != null) {
			return floor.getPosition().x * PPM + floor.getWidth() / 2;
		} else {
			return 0.0f;
		}
	}
}
