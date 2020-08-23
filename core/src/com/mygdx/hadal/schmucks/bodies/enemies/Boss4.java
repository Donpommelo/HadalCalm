package com.mygdx.hadal.schmucks.bodies.enemies;

import java.util.concurrent.ThreadLocalRandom;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.ParticleColor;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.EnemyUtils;
import com.mygdx.hadal.event.SpawnerSchmuck;
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity.particleSyncType;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.bodies.hitboxes.RangedHitbox;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.strategies.HitboxStrategy;
import com.mygdx.hadal.strategies.hitbox.AdjustAngle;
import com.mygdx.hadal.strategies.hitbox.ContactUnitBurn;
import com.mygdx.hadal.strategies.hitbox.ContactUnitParticles;
import com.mygdx.hadal.strategies.hitbox.ContactWallDie;
import com.mygdx.hadal.strategies.hitbox.ContactWallLoseDurability;
import com.mygdx.hadal.strategies.hitbox.ContactWallParticles;
import com.mygdx.hadal.strategies.hitbox.ControllerDefault;
import com.mygdx.hadal.strategies.hitbox.CreateParticles;
import com.mygdx.hadal.strategies.hitbox.DamageStandard;
import com.mygdx.hadal.strategies.hitbox.DamageStatic;
import com.mygdx.hadal.strategies.hitbox.FixedToEntity;
import com.mygdx.hadal.strategies.hitbox.HomingUnit;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.Stats;

/**
 * This is a boss in the game
 * @author Zachary Tu
 */
public class Boss4 extends EnemyFloating {
	
	private final static String name = "BOSS4";

    private static final float aiAttackCd = 2.4f;
    private static final float aiAttackCd2 = 2.0f;
    private static final float aiAttackCd3 = 1.6f;
	
    private final static int scrapDrop = 15;
    
	private static final int width = 400;
	private static final int height = 400;
	
	private static final int hbWidth = 400;
	private static final int hbHeight = 400;
	
	private static final float scale = 1.0f;
	
	private static final int hp = 7500;
	
	private static final Sprite sprite = Sprite.NOTHING;
	
	//these control the scaling of the boss' body particles
	private float currentScale = 1.0f;
	private float desiredScale = 1.0f;
	private float scaleLerpFactor = 0.2f;
		
	private int phase = 1;
	private static final float phaseThreshold2 = 0.8f;
	private static final float phaseThreshold3 = 0.4f;
	
	private ParticleEntity body1, body2, body3;
	private static final float bodyBaseScale1 = 2.5f;
	private static final float bodyBaseScale2 = 2.5f;
	private static final float bodyBaseScale3 = 5.0f;
	
	public Boss4(PlayState state, Vector2 startPos, short filter, SpawnerSchmuck spawner) {
		super(state, startPos, new Vector2(width, height).scl(scale), new Vector2(hbWidth, hbHeight).scl(scale), name, sprite, EnemyType.BOSS4, filter, hp, aiAttackCd, scrapDrop, spawner);

		body1 = new ParticleEntity(state, this, Particle.WORMHOLE, 1.0f, 0.0f, true, particleSyncType.TICKSYNC) {
			
			@Override
			public void render(SpriteBatch batch) {}
			
		};
		
		body1.setScale(bodyBaseScale1).setColor(ParticleColor.RED).setSyncExtraFields(true);
		body2 = new ParticleEntity(state, this, Particle.STORM, 1.0f, 0.0f, true, particleSyncType.TICKSYNC) {
			
			@Override
			public void render(SpriteBatch batch) {}
			
		};
		body2.setScale(bodyBaseScale2).setColor(ParticleColor.ORANGE).setSyncExtraFields(true);
		
		body3 = new ParticleEntity(state, this, Particle.BRIGHT, 1.0f, 0.0f, true, particleSyncType.TICKSYNC) {
			
			@Override
			public void render(SpriteBatch batch) {}
		};
		body3.setScale(bodyBaseScale3).setColor(ParticleColor.RED).setSyncExtraFields(true);
	}
	
	
	private static final float charge1Damage = 1.5f;
	private static final float attackInterval = 0.2f;
	private static final int defaultMeleeKB = 5;
	@Override
	public void create() {
		super.create();
		
		body.setType(BodyType.KinematicBody);
		
		getBodyData().addStatus(new StatChangeStatus(state, Stats.KNOCKBACK_RES, 1.0f, getBodyData()));
		
		EnemyUtils.meleeAttackContinuous(state, this, charge1Damage, attackInterval, defaultMeleeKB, 0.0f, true);
	}

	private float scalingAccumulator;
	private final static float scalingTime = 1 / 120f;
	@Override
	public void controller(float delta) {
		super.controller(delta);
		
		//Update the game camera.
		scalingAccumulator += delta;
		
		while (scalingAccumulator >= scalingTime) {
			
			scalingAccumulator -= scalingTime;
					
			if (currentScale != desiredScale) {
				currentScale += (desiredScale - currentScale) * scaleLerpFactor;
				
				body1.setScale(currentScale * bodyBaseScale1);
				body2.setScale(currentScale * bodyBaseScale2);
				body3.setScale(currentScale * bodyBaseScale3);
			}
		}
	}
	
	@Override
	public void render(SpriteBatch batch) {
		body1.getEffect().draw(batch, Gdx.graphics.getDeltaTime());
		body2.getEffect().draw(batch, Gdx.graphics.getDeltaTime());
		body3.getEffect().draw(batch, Gdx.graphics.getDeltaTime());
	}
	
	@Override
	public void multiplayerScaling(int numPlayers) {
		getBodyData().addStatus(new StatChangeStatus(state, Stats.MAX_HP, 1000 * numPlayers, getBodyData()));
	}
	
	private int attackNum = 0;
	@Override
	public void attackInitiate() {
		attackNum++;
		
		if (phase == 1) {
			if (getBodyData().getCurrentHp() <= phaseThreshold2 * getBodyData().getStat(Stats.MAX_HP)) {
				phase = 2;
				setAttackCd(aiAttackCd2);
			} else {
				int randomIndex = GameStateManager.generator.nextInt(4);

				switch(randomIndex) {
				case 0: 
					radialShot1();
					break;
				case 1: 
					twinFlameSpin();
					break;
				case 2: 
					bigBangBell();
					break;
				case 3: 
					bounceLaser();
				break;
				}
			}
		}
		
		if (phase == 2) {
			if (getBodyData().getCurrentHp() <= phaseThreshold3 * getBodyData().getStat(Stats.MAX_HP)) {
				phase = 3;
				setAttackCd(aiAttackCd3);
			} else if (attackNum % 2 == 0) {

			} else {

			}
		}
		
		if (phase == 3) {
			if (attackNum % 2 == 0) {

			} else {

			}
		}
	}
	
	private static final float particleLinger = 3.0f;
	private static final float shot1Windup = 1.5f;
	private static final float projSpeed = 18.0f;
	private static final int numShots = 12;
	
	private static final float shot1Damage = 15.0f;
	private static final float shot1Lifespan = 3.0f;
	private static final float shot1Knockback = 20.0f;

	private static final Vector2 projSize = new Vector2(120, 60);
	
	Vector2 angle = new Vector2(1, 0);
	private void radialShot1() {
		changeColor(ParticleColor.PURPLE, shot1Windup);
		singlePulse();
		
		getActions().add(new EnemyAction(this, 0.0f) {
			
			@Override
			public void execute() {
				
				for (int i = 0; i < numShots; i++) {
					angle.setAngle(angle.angle() + 360 / numShots);
					
					Vector2 startVelo = new Vector2(projSpeed, projSpeed).setAngle(angle.angle());
					RangedHitbox hbox = new RangedHitbox(state, enemy.getProjectileOrigin(startVelo, projSize.x), projSize, shot1Lifespan, startVelo, enemy.getHitboxfilter(), true, false, enemy, Sprite.LASER_PURPLE);
					
					hbox.setPassability((short) (Constants.BIT_PROJECTILE | Constants.BIT_WALL | Constants.BIT_PLAYER | Constants.BIT_ENEMY));
					
					hbox.addStrategy(new ControllerDefault(state, hbox, enemy.getBodyData()));
					hbox.addStrategy(new DamageStandard(state, hbox, enemy.getBodyData(), shot1Damage, shot1Knockback, DamageTypes.RANGED));
					hbox.addStrategy(new AdjustAngle(state, hbox, enemy.getBodyData()));
					hbox.addStrategy(new ContactWallParticles(state, hbox, enemy.getBodyData(), Particle.LASER_IMPACT).setOffset(true).setParticleColor(ParticleColor.PURPLE));
					hbox.addStrategy(new ContactUnitParticles(state, hbox, enemy.getBodyData(), Particle.LASER_IMPACT).setOffset(true).setParticleColor(ParticleColor.PURPLE));
					hbox.addStrategy(new ContactWallDie(state, hbox, enemy.getBodyData()));
					hbox.addStrategy(new CreateParticles(state, hbox, enemy.getBodyData(), Particle.LASER_TRAIL, 0.0f, particleLinger).setParticleColor(ParticleColor.PURPLE));
				}
			}
		});
		
		singlePulseReturn();
		changeColor(ParticleColor.RED, 0.0f);
	}
	
	private static final float fireWindup = 0.75f;

	private static final int fireballNumber = 120;
	private static final float fireballInterval = 0.05f;
	
	private static final int fireballDamage = 4;
	private static final int burnDamage = 3;
	private static final int fireSpeed = 10;
	private static final int fireKB = 10;
	private static final float fireLifespan = 2.0f;
	private static final float burnDuration = 4.0f;
	
	private static final Vector2 fireSize = new Vector2(80, 80);
	private static final Vector2 windupSize = new Vector2(120, 120);
	
	private void twinFlameSpin() {
		changeColor(ParticleColor.ORANGE, shot1Windup);
		singlePulse();
		singlePulseReturn();

		final float startAngle = getAttackAngle();
		
		windupParticle(startAngle, Particle.FIRE, ParticleColor.NOTHING, 40.0f, fireWindup, 0.0f);
		windupParticle(startAngle + 180, Particle.FIRE, ParticleColor.NOTHING, 40.0f, fireWindup, fireWindup);
		
		for (int i = 0; i < fireballNumber; i++) {
			
			final int index = i;
			
			getActions().add(new EnemyAction(this, fireballInterval) {
				
				@Override
				public void execute() {
					
					Vector2 startVelo1 = new Vector2(fireSpeed, fireSpeed).setAngle(startAngle + index * 360 / fireballNumber);
					RangedHitbox hbox1 = new RangedHitbox(state, enemy.getProjectileOrigin(startVelo1, fireSize.x), fireSize, fireLifespan, startVelo1, enemy.getHitboxfilter(), true, false, enemy, Sprite.NOTHING);
					
					hbox1.setPassability((short) (Constants.BIT_PROJECTILE | Constants.BIT_WALL | Constants.BIT_PLAYER | Constants.BIT_ENEMY));
					
					hbox1.addStrategy(new ControllerDefault(state, hbox1, enemy.getBodyData()));
					hbox1.addStrategy(new ContactUnitBurn(state, hbox1, enemy.getBodyData(), burnDuration, burnDamage));
					hbox1.addStrategy(new DamageStandard(state, hbox1, enemy.getBodyData(), fireballDamage, fireKB, DamageTypes.RANGED, DamageTypes.FIRE));
					hbox1.addStrategy(new ContactWallDie(state, hbox1, enemy.getBodyData()));
					hbox1.addStrategy(new CreateParticles(state, hbox1, enemy.getBodyData(), Particle.FIRE, 0.0f, particleLinger).setParticleSize(40.0f));
					
					Vector2 startVelo2 = new Vector2(fireSpeed, fireSpeed).setAngle(startAngle + index * 360 / fireballNumber + 180);
					RangedHitbox hbox2 = new RangedHitbox(state, enemy.getProjectileOrigin(startVelo2, fireSize.x), fireSize, fireLifespan, startVelo2, enemy.getHitboxfilter(), true, false, enemy, Sprite.NOTHING);
					
					hbox2.setPassability((short) (Constants.BIT_PROJECTILE | Constants.BIT_WALL | Constants.BIT_PLAYER | Constants.BIT_ENEMY));
					
					hbox2.addStrategy(new ControllerDefault(state, hbox2, enemy.getBodyData()));
					hbox2.addStrategy(new ContactUnitBurn(state, hbox2, enemy.getBodyData(), burnDuration, burnDamage));
					hbox2.addStrategy(new DamageStandard(state, hbox2, enemy.getBodyData(), fireballDamage, fireKB, DamageTypes.RANGED, DamageTypes.FIRE));
					hbox2.addStrategy(new ContactWallDie(state, hbox2, enemy.getBodyData()));
					hbox2.addStrategy(new CreateParticles(state, hbox2, enemy.getBodyData(), Particle.FIRE, 0.0f, particleLinger).setParticleSize(40.0f));
				}
			});
		}
		
		changeColor(ParticleColor.RED, 0.0f);
	}
	
	private static final Vector2 bellSize = new Vector2(300, 300);
	private static final float bellSpeed = 15.0f;
	private static final float bellDamage = 1.5f;
	private static final float bellHomingSpeed = 20.0f;
	private static final float bellKB = 2.5f;
	private static final float bellLifespan = 12.0f;
	
	private static final float bellInterval = 0.02f;
	
	private void bigBangBell() {
		changeColor(ParticleColor.YELLOW, shot1Windup);
		singlePulse();
		
		getActions().add(new EnemyAction(this, 0.0f) {
			
			@Override
			public void execute() {
				Hitbox bell = new Hitbox(state, enemy.getPixelPosition(), bellSize, bellLifespan, new Vector2(0, bellSpeed), enemy.getHitboxfilter(), false, false, enemy, Sprite.ORB_YELLOW);
				bell.setPassability((short) (Constants.BIT_PROJECTILE | Constants.BIT_WALL | Constants.BIT_PLAYER | Constants.BIT_ENEMY));

				bell.setRestitution(0.2f);
				
				bell.addStrategy(new ControllerDefault(state, bell, enemy.getBodyData()));
				bell.addStrategy(new CreateParticles(state, bell, enemy.getBodyData(), Particle.LIGHTNING, 0.0f, particleLinger).setParticleSize(30.0f));
				bell.addStrategy(new HomingUnit(state, bell, enemy.getBodyData(), bellHomingSpeed, enemy.getHitboxfilter()));
				
				bell.addStrategy((new HitboxStrategy(state, bell, enemy.getBodyData()) {
					
					private float controllerCount = 0;
				
					@Override
					public void controller(float delta) {
						
						controllerCount += delta;
						
						while (controllerCount >= bellInterval) {
							controllerCount -= bellInterval;
							
							Hitbox pulse = new Hitbox(state, hbox.getPixelPosition(), hbox.getSize(), bellInterval, new Vector2(0, 0), enemy.getHitboxfilter(), true, false, enemy, Sprite.NOTHING);
							pulse.setSyncDefault(false);
							pulse.makeUnreflectable();
							pulse.addStrategy(new ControllerDefault(state, pulse, enemy.getBodyData()));
							pulse.addStrategy(new DamageStatic(state, pulse, enemy.getBodyData(), bellDamage, bellKB, DamageTypes.MELEE));
							pulse.addStrategy(new FixedToEntity(state, pulse, enemy.getBodyData(), bell, new Vector2(), new Vector2(), true));
						}
					}
				}));
			}
		});
		
		singlePulseReturn();
		changeColor(ParticleColor.RED, 0.0f);
	}
	
	private static final int laserSpread = 4;
	
	private static final int trailNumber = 5;
	private static final float trailInterval = 0.5f;
	
	private final static Vector2 trailSize = new Vector2(180, 90);
	private final static float trailSpeed = 150.0f;
	private final static float trailLifespan = 10.0f;
	
	private static final int laserNumber = 40;
	private static final float laserInterval = 0.05f;
	
	private final static Vector2 laserSize = new Vector2(180, 90);
	private final static float laserSpeed = 90.0f;
	private final static float laserDamage = 6.0f;
	private final static float laserKB = 12.0f;
	
	private final static int beamDurability = 9;
	
	private final static int[] startingVelos = {30, 60, 120, 150, 210, 240, 300, 330};
	
	private void bounceLaser() {
		changeColor(ParticleColor.BLUE, shot1Windup);
		
		final float startAngle = startingVelos[GameStateManager.generator.nextInt(startingVelos.length)] + ThreadLocalRandom.current().nextInt(-laserSpread, laserSpread + 1);
		
		windupParticle(startAngle, Particle.CHARGING, ParticleColor.BLUE, 30.0f, trailNumber * trailInterval, 0.0f);
		
		Vector2 startVeloTrail = new Vector2(trailSpeed, trailSpeed).setAngle(startAngle);
		
		for (int i = 0; i < trailNumber; i++) {
			getActions().add(new EnemyAction(this, trailInterval) {
				
				@Override
				public void execute() {
					
					
					Hitbox trail = new RangedHitbox(state, getPixelPosition(), trailSize, trailLifespan, startVeloTrail, enemy.getHitboxfilter(), false, false, enemy, Sprite.NOTHING);
					trail.setPassability((short) (Constants.BIT_PROJECTILE | Constants.BIT_WALL | Constants.BIT_PLAYER | Constants.BIT_ENEMY));
					trail.setDurability(beamDurability);
					trail.setRestitution(1.0f);

					trail.addStrategy(new ControllerDefault(state, trail, getBodyData()));
					trail.addStrategy(new AdjustAngle(state, trail, getBodyData()));
					trail.addStrategy(new ContactWallLoseDurability(state, trail, getBodyData()));
					trail.addStrategy(new CreateParticles(state, trail, getBodyData(), Particle.LASER_TRAIL, 0.0f, 3.0f).setParticleSize(40.0f));
				}
			});
		}
		
		windupParticle(startAngle, Particle.OVERCHARGE, ParticleColor.BLUE, 30.0f, laserNumber * laserInterval, 0.0f);
		
		Vector2 startVeloLaser = new Vector2(laserSpeed, laserSpeed).setAngle(startAngle);
		Vector2 startPosLaser = new Vector2(getPixelPosition()).add(new Vector2(0, getHboxSize().x / 2 + WindupOffset).setAngle(startAngle));
		
		for (int i = 0; i < laserNumber; i++) {
			getActions().add(new EnemyAction(this, laserInterval) {
				
				@Override
				public void execute() {
					
					Hitbox laser = new RangedHitbox(state, startPosLaser, laserSize, trailLifespan, startVeloLaser, enemy.getHitboxfilter(), false, false, enemy, Sprite.LASER_BLUE);
					laser.setPassability((short) (Constants.BIT_PROJECTILE | Constants.BIT_WALL | Constants.BIT_PLAYER | Constants.BIT_ENEMY));
					laser.setDurability(beamDurability);
					laser.setRestitution(1.0f);
					
					laser.addStrategy(new ControllerDefault(state, laser, getBodyData()));
					laser.addStrategy(new AdjustAngle(state, laser, getBodyData()));
					laser.addStrategy(new ContactWallParticles(state, laser, getBodyData(), Particle.LASER_IMPACT).setOffset(true).setParticleColor(ParticleColor.BLUE));
					laser.addStrategy(new ContactUnitParticles(state, laser, getBodyData(), Particle.LASER_IMPACT).setOffset(true).setParticleColor(ParticleColor.BLUE));
					laser.addStrategy(new ContactWallLoseDurability(state, laser, getBodyData()));
					laser.addStrategy(new DamageStandard(state, laser, enemy.getBodyData(), laserDamage, laserKB, DamageTypes.RANGED, DamageTypes.ENERGY));
				}
			});
		}
		
		changeColor(ParticleColor.RED, 0.0f);
	}
	
	private void deadStarSigh() {
		
	}
	
	private static final float shrinkDurationIn = 1.0f;
	private static final float shrinkLerpIn = 0.08f;
	private static final float shrinkScaleIn = 0.2f;
	
	private static final float shrinkDurationOut = 0.2f;
	private static final float shrinkLerpOut = 0.2f;
	private static final float shrinkScaleOut = 1.5f;
	
	private static final float shrinkDurationReturn = 1.0f;
	private static final float shrinkLerpReturn = 0.04f;
	private static final float shrinkScaleReturn = 1.0f;

	private void singlePulse() {
		scaleSize(shrinkScaleIn, shrinkLerpIn, shrinkDurationIn);
		scaleSize(shrinkScaleOut, shrinkLerpOut, shrinkDurationOut);
	}
	
	private void singlePulseReturn() {
		scaleSize(shrinkScaleReturn, shrinkLerpReturn, shrinkDurationReturn);
	}
	
	private void scaleSize(float scale, float lerpFactor, float duration) {
		getActions().add(new EnemyAction(this, duration) {
			
			@Override
			public void execute() {
				desiredScale = scale;
				scaleLerpFactor = lerpFactor;
			}
		});
	}
	
	private void changeColor(ParticleColor color, float duration) {
		getActions().add(new EnemyAction(this, duration) {
			
			@Override
			public void execute() {
				body3.setColor(color);
			}
		});
	}
	
	private static final float WindupOffset = 15.0f;
	private void windupParticle(float startAngle, Particle particle, ParticleColor color, float particleScale, float lifespan, float duration) {
		
		getActions().add(new EnemyAction(this, duration) {
			
			private Vector2 addVector = new Vector2();
			@Override
			public void execute() {
				Vector2 startVelo1 = new Vector2(0, getHboxSize().x / 2 + WindupOffset).setAngle(startAngle);
				Hitbox hbox1 = new Hitbox(state, enemy.getPixelPosition(), windupSize, lifespan, startVelo1, enemy.getHitboxfilter(), true, false, enemy, Sprite.NOTHING);

				hbox1.addStrategy(new ControllerDefault(state, hbox1, enemy.getBodyData()));
				hbox1.addStrategy(new CreateParticles(state, hbox1, enemy.getBodyData(), particle, 0.0f, particleLinger).setParticleColor(color).setParticleSize(particleScale));
				hbox1.addStrategy(new HitboxStrategy(state, hbox1, enemy.getBodyData()) {
					
					@Override
					public void controller(float delta) {
						if (!enemy.isAlive()) {
							hbox.die();
						} else {
							hbox.setTransform(addVector.set(enemy.getPixelPosition()).add(startVelo1).scl(1.0f / 32), 0);
						}
					}
				});
			}
		});
	}
}
