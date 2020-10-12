package com.mygdx.hadal.schmucks.bodies.enemies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.ParticleColor;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.EnemyUtils;
import com.mygdx.hadal.equip.WeaponUtils;
import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.event.SpawnerSchmuck;
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity.particleSyncType;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.schmucks.bodies.SoundEntity;
import com.mygdx.hadal.schmucks.bodies.SoundEntity.soundSyncType;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.bodies.hitboxes.RangedHitbox;
import com.mygdx.hadal.server.User;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.strategies.HitboxStrategy;
import com.mygdx.hadal.strategies.hitbox.*;
import com.mygdx.hadal.utils.Stats;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

/**
 * This is a boss in the game
 * @author Zachary Tu
 */
public class Boss4 extends EnemyFloating {
	
	private static final String name = "FALSE SUN";

    private static final float aiAttackCd = 2.2f;
    private static final float aiAttackCd2 = 1.6f;
	
    private static final int scrapDrop = 15;
    
	private static final int width = 360;
	private static final int height = 360;
	
	private static final int hbWidth = 360;
	private static final int hbHeight = 360;
	
	private static final float scale = 1.0f;
	
	private static final int hp = 11000;
	
	private static final Sprite sprite = Sprite.NOTHING;
	
	//these control the scaling of the boss' body particles
	private float currentScale = 1.0f;
	private float desiredScale = 1.0f;
	private float scaleLerpFactor = 0.2f;
		
	private int phase = 1;
	private static final float phaseThreshold2 = 0.5f;
	
	//the boss's body is composed of multiple scaled up particle effects
	private final ParticleEntity body1, body2, body3;
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
	private static final float attackInterval = 0.1f;
	private static final int defaultMeleeKB = 3;
	@Override
	public void create() {
		super.create();
		
		body.setType(BodyType.KinematicBody);
		
		getBodyData().addStatus(new StatChangeStatus(state, Stats.KNOCKBACK_RES, 1.0f, getBodyData()));
		
		//this boss continuously damages players that contact it
		EnemyUtils.meleeAttackContinuous(state, this, charge1Damage, attackInterval, defaultMeleeKB, 0.0f, true);
	}

	private float scalingAccumulator;
	private static final float scalingTime = 1 / 120f;
	@Override
	public void controller(float delta) {
		super.controller(delta);
		
		//the boss grows and shrinks by rescaling its particles. This occurs before it performs actions
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
		if (state.isServer()) {
			body1.getEffect().draw(batch, Gdx.graphics.getDeltaTime());
			body2.getEffect().draw(batch, Gdx.graphics.getDeltaTime());
			body3.getEffect().draw(batch, Gdx.graphics.getDeltaTime());
		}
	}
	
	@Override
	public void multiplayerScaling(int numPlayers) {
		getBodyData().addStatus(new StatChangeStatus(state, Stats.MAX_HP, 3000 * numPlayers, getBodyData()));
	}
	
	private int attackNum;
	@Override
	public void attackInitiate() {
		attackNum++;
		if (phase == 1) {
			if (getBodyData().getCurrentHp() <= phaseThreshold2 * getBodyData().getStat(Stats.MAX_HP)) {
				phase = 2;
				setAttackCd(aiAttackCd2);
				randomReticleWave();
				teleport();
				attacks1.clear();
				attacks2.clear();
			} else {
				phase1Attack();
			}
		} else if (phase == 2) {
			phase2Attack();
		}
	}
	
	private static final int phase1NumAttacks = 3;
	private static final int phase2NumAttacks = 5;

	//these lists are used to make the boss perform all attacks in its pool before repeating any
	private final ArrayList<Integer> attacks1 = new ArrayList<>();
	private final ArrayList<Integer> attacks2 = new ArrayList<>();
	private void phase1Attack() {
		if (attacks1.isEmpty()) {
			for (int i = 0; i < phase1NumAttacks; i++) {
				attacks1.add(i);
			}
		}
		if (attacks2.isEmpty()) {
			for (int i = 0; i < phase1NumAttacks; i++) {
				attacks2.add(i);
			}
		}
		
		if (attackNum % 2 == 0) {
			int nextAttack = attacks1.remove(GameStateManager.generator.nextInt(attacks1.size()));
			switch(nextAttack) {
			case 0: 
				reticleShots();
				break;
			case 1: 
				deadStarSigh();
				break;
			case 2: 
				bigBangBell();
				break;
			}
		} else {
			int nextAttack = attacks2.remove(GameStateManager.generator.nextInt(attacks2.size()));
			switch(nextAttack) {
			case 0: 
				radialShot1();
				break;
			case 1: 
				twinFlameSpin();
				break;
			case 2: 
				bounceLaser();
				break;
			}
		}
	}
	
	
	private void phase2Attack() {
		if (attacks1.isEmpty()) {
			for (int i = 0; i < phase2NumAttacks; i++) {
				attacks1.add(i);
			}
		}
		
		int nextAttack = attacks1.remove(GameStateManager.generator.nextInt(attacks1.size()));
		switch(nextAttack) {
		case 0: 
			apocalypseLaser();
			break;
		case 1: 
			horizontalBullets();
			break;
		case 2: 
			orbitalStar();
			break;
		case 3: 
			randomReticleWave();
			break;
		case 4: 
			willOWisp();
			break;
		}
	}
	
	private static final float particleLinger = 1.0f;
	private static final float shot1Windup = 1.5f;
	private static final int numShots = 12;
	
	private static final float shot1Damage = 24.0f;
	private static final float shot1Lifespan = 5.0f;
	private static final float shot1Knockback = 20.0f;
	private static final float shot1Speed = 12.0f;
	private static final float returnAmp = 90.0f;
	private static final float pushInterval = 1.5f;

	private static final Vector2 projSize = new Vector2(80, 40);
	private static final Vector2 projSpriteSize = new Vector2(120, 60);
	
	Vector2 angle = new Vector2(1, 0);
	private void radialShot1() {
		changeColor(ParticleColor.VIOLET, shot1Windup);
		singlePulse();
		
		getActions().add(new EnemyAction(this, 0.0f) {
			
			@Override
			public void execute() {
				SoundEffect.MAGIC3_BURST.playUniversal(state, getPixelPosition(), 0.9f, 0.75f, false);
				
				for (int i = 0; i < numShots; i++) {
					angle.setAngle(angle.angle() + 360.0f / numShots);
					
					Vector2 startVelo = new Vector2(shot1Speed, 0).setAngle(angle.angle());
					RangedHitbox hbox = new RangedHitbox(state, getProjectileOrigin(startVelo, projSize.x), projSize, shot1Lifespan, startVelo, getHitboxfilter(), true, false, enemy, Sprite.LASER_PURPLE);
					hbox.setSpriteSize(projSpriteSize);
					hbox.setAdjustAngle(true);
					
					hbox.addStrategy(new ControllerDefault(state, hbox, getBodyData()));
					hbox.addStrategy(new DamageStandard(state, hbox, getBodyData(), shot1Damage, shot1Knockback, DamageTypes.RANGED));
					
					hbox.addStrategy(new ReturnToUser(state, hbox, getBodyData(), returnAmp));
					hbox.addStrategy(new CreateParticles(state, hbox, getBodyData(), Particle.LASER_TRAIL, 0.0f, particleLinger).setParticleColor(ParticleColor.VIOLET));
					hbox.addStrategy(new ContactUnitParticles(state, hbox, getBodyData(), Particle.LASER_IMPACT).setOffset(true).setParticleColor(ParticleColor.VIOLET));
					hbox.addStrategy(new ContactUnitSound(state, hbox, getBodyData(), SoundEffect.DAMAGE3, 0.6f, true));
					hbox.addStrategy(new ContactUnitDie(state, hbox, getBodyData()));
					hbox.addStrategy((new HitboxStrategy(state, hbox, getBodyData()) {
						
						private float controllerCount = pushInterval;
						private final Vector2 push = new Vector2(startVelo);
						@Override
						public void controller(float delta) {
							
							controllerCount += delta;
							
							//radial shots continuously move towards the player while being periodically pushed outwards to make it pulsate.
							while (controllerCount >= pushInterval) {
								controllerCount -= pushInterval;
								hbox.setLinearVelocity(push.scl(1.5f));
							}
						}
					}));
				}
			}
		});
		
		singlePulseReturn();
		changeColor(ParticleColor.RED, 0.0f);
	}
	
	private static final float fireWindup = 0.5f;

	private static final int fireballNumber = 80;
	private static final float fireballInterval = 0.075f;
	
	private static final int fireballDamage = 6;
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
		EnemyUtils.createSoundEntity(state, this, 0.0f, fireballNumber * fireballInterval, 0.6f, 2.0f, SoundEffect.FLAMETHROWER, true);
		for (int i = 0; i < fireballNumber; i++) {
			
			final int index = i;
			
			getActions().add(new EnemyAction(this, fireballInterval) {
				
				@Override
				public void execute() {
					
					Vector2 startVelo1 = new Vector2(fireSpeed, fireSpeed).setAngle(startAngle + index * 360.0f / fireballNumber);
					fireball(startVelo1);
					
					Vector2 startVelo2 = new Vector2(fireSpeed, fireSpeed).setAngle(startAngle + index * 360.0f / fireballNumber + 180);
					fireball(startVelo2);
				}
				
				public void fireball(Vector2 startVelo) {
					RangedHitbox hbox = new RangedHitbox(state, getProjectileOrigin(startVelo, fireSize.x), fireSize, fireLifespan, startVelo, getHitboxfilter(), true, false, enemy, Sprite.NOTHING);
					
					hbox.addStrategy(new ControllerDefault(state, hbox, getBodyData()));
					hbox.addStrategy(new ContactUnitBurn(state, hbox, getBodyData(), burnDuration, burnDamage));
					hbox.addStrategy(new DamageStandard(state, hbox, getBodyData(), fireballDamage, fireKB, DamageTypes.RANGED, DamageTypes.FIRE));
					hbox.addStrategy(new ContactWallDie(state, hbox, getBodyData()));
					hbox.addStrategy(new CreateParticles(state, hbox, getBodyData(), Particle.FIRE, 0.0f, particleLinger).setParticleSize(36.0f));
				}
			});
		}
		
		changeColor(ParticleColor.RED, 0.0f);
	}
	
	private static final Vector2 bellSize = new Vector2(225, 225);
	private static final Vector2 bellSpriteSize = new Vector2(300, 300);
	private static final float bellSpeed = 15.0f;
	private static final float bellDamage = 4.5f;
	private static final float bellHomingSpeed = 30.0f;
	private static final float bellKB = 1.0f;
	private static final float bellLifespan = 12.0f;
	
	private static final float bellInterval = 0.06f;
	
	private void bigBangBell() {
		changeColor(ParticleColor.GOLD, shot1Windup);
		singlePulse();
		
		getActions().add(new EnemyAction(this, 0.0f) {
			
			@Override
			public void execute() {
				SoundEffect.DOORBELL.playUniversal(state, getPixelPosition(), 1.0f, 0.6f, false);
				
				Hitbox bell = new Hitbox(state, getPixelPosition(), bellSize, bellLifespan, new Vector2(0, bellSpeed), getHitboxfilter(), false, false, enemy, Sprite.ORB_YELLOW);
				bell.setSpriteSize(bellSpriteSize);
				bell.setRestitution(0.2f);
				
				bell.addStrategy(new ControllerDefault(state, bell, getBodyData()));
				bell.addStrategy(new CreateParticles(state, bell, getBodyData(), Particle.LIGHTNING, 0.0f, particleLinger).setParticleSize(30.0f));
				bell.addStrategy(new HomingUnit(state, bell, getBodyData(), bellHomingSpeed, getHitboxfilter()));
				
				bell.addStrategy((new HitboxStrategy(state, bell, getBodyData()) {
					
					private float controllerCount = 0;
				
					@Override
					public void controller(float delta) {
						
						controllerCount += delta;
						
						//hitbox continuously damages players that touch it
						while (controllerCount >= bellInterval) {
							controllerCount -= bellInterval;
							
							Hitbox pulse = new Hitbox(state, hbox.getPixelPosition(), hbox.getSize(), bellInterval, new Vector2(0, 0), enemy.getHitboxfilter(), true, false, enemy, Sprite.NOTHING);
							pulse.setSyncDefault(false);
							pulse.makeUnreflectable();
							pulse.addStrategy(new ControllerDefault(state, pulse, getBodyData()));
							pulse.addStrategy(new DamageStatic(state, pulse, getBodyData(), bellDamage, bellKB, DamageTypes.MELEE));
							pulse.addStrategy(new FixedToEntity(state, pulse, getBodyData(), bell, new Vector2(), new Vector2(), true));
							pulse.addStrategy(new ContactUnitSound(state, pulse, getBodyData(), SoundEffect.ZAP, 0.6f, true));
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
	
	private static final Vector2 trailSize = new Vector2(120, 60);
	private static final float trailSpeed = 200.0f;
	private static final float trailLifespan = 10.0f;
	
	private static final int laserNumber = 40;
	private static final float laserInterval = 0.05f;
	
	private static final Vector2 laserSpriteSize = new Vector2(180, 90);
	private static final Vector2 laserSize = new Vector2(120, 60);
	private static final float laserSpeed = 125.0f;
	private static final float laserDamage = 7.5f;
	private static final float laserKB = 12.0f;
	
	private static final int beamDurability = 9;
	
	private static final int[] startingVelos = {30, 60, 120, 150, 210, 240, 300, 330};
	
	private void bounceLaser() {
		changeColor(ParticleColor.BLUE, shot1Windup);
		
		final float startAngle = startingVelos[GameStateManager.generator.nextInt(startingVelos.length)] + ThreadLocalRandom.current().nextInt(-laserSpread, laserSpread + 1);
		
		Vector2 startVeloTrail = new Vector2(0, trailSpeed).setAngle(startAngle);
		Vector2 startPosLaser = new Vector2(getPixelPosition()).add(new Vector2(0, getHboxSize().x / 2 + WindupOffset).setAngle(startAngle));
		
		//the boss first sends out a series of trails that mark a path
		for (int i = 0; i < trailNumber; i++) {
			getActions().add(new EnemyAction(this, trailInterval) {
				
				@Override
				public void execute() {
					
					Hitbox trail = new RangedHitbox(state, startPosLaser, trailSize, trailLifespan, startVeloTrail, getHitboxfilter(), false, false, enemy, Sprite.NOTHING);
					trail.setDurability(beamDurability);
					trail.setRestitution(1.0f);

					trail.addStrategy(new ControllerDefault(state, trail, getBodyData()));
					trail.addStrategy(new AdjustAngle(state, trail, getBodyData()));
					trail.addStrategy(new ContactWallLoseDurability(state, trail, getBodyData()));
					trail.addStrategy(new CreateParticles(state, trail, getBodyData(), Particle.LASER_TRAIL, 0.0f, particleLinger).setParticleSize(40.0f));
				}
			});
		}
		
		windupParticle(startAngle, Particle.OVERCHARGE, ParticleColor.BLUE, 30.0f, laserNumber * laserInterval, 0.0f);
		
		Vector2 startVeloLaser = new Vector2(0, laserSpeed).setAngle(startAngle);
		
		EnemyUtils.createSoundEntity(state, this, 0.0f, laserNumber * laserInterval, 1.0f, 2.0f, SoundEffect.BEAM3, true);
		
		//next, the boss fires a laser along the marked path
		for (int i = 0; i < laserNumber; i++) {
			getActions().add(new EnemyAction(this, laserInterval) {
				
				@Override
				public void execute() {
					
					Hitbox laser = new RangedHitbox(state, startPosLaser, laserSize, trailLifespan, startVeloLaser, getHitboxfilter(), false, false, enemy, Sprite.LASER_BLUE);
					laser.setSpriteSize(laserSpriteSize);
					laser.setDurability(beamDurability);
					laser.setRestitution(1.0f);
					
					laser.addStrategy(new ControllerDefault(state, laser, getBodyData()));
					laser.addStrategy(new AdjustAngle(state, laser, getBodyData()));
					laser.addStrategy(new DieParticles(state, laser, getBodyData(), Particle.LASER_IMPACT).setParticleColor(ParticleColor.BLUE));
					laser.addStrategy(new ContactWallLoseDurability(state, laser, getBodyData()));
					laser.addStrategy(new DamageStandard(state, laser, getBodyData(), laserDamage, laserKB, DamageTypes.RANGED, DamageTypes.ENERGY));
					laser.addStrategy(new ContactUnitSound(state, laser, getBodyData(), SoundEffect.DAMAGE3, 0.6f, true));
				}
			});
		}
		
		changeColor(ParticleColor.RED, 0.0f);
	}
	
	private static final int sighNumber = 5;
	private static final int sighSpread = 60;
	private static final float sighInterval = 0.4f;
	private static final float sighLifespan = 3.0f;
	
	private static final float cloudDelay = 1.0f;
	private static final Vector2 cloudSize = new Vector2(120, 120);
	private static final float cloudInterval = 0.1f;
	private static final float cloudLifespan = 0.75f;
	private static final float cloudSpeed = 60.0f;
	private static final float cloudDamage = 6.0f;
	private static final float cloudKB = 5.0f;
	
	private static final float slowDuration = 9.0f;
	private static final float slowSlow = 0.8f;
	
	private void deadStarSigh() {
		changeColor(ParticleColor.TURQOISE, shot1Windup);
		singlePulse();
		singlePulseReturn();
		
		for (int i = 0; i < sighNumber; i++) {
			
			final int index = i;
			
			//the boss first creates a number of clouds along its perimeter
			getActions().add(new EnemyAction(this, sighInterval) {
				
				@Override
				public void execute() {
					
					float startAngle = getAttackAngle() + ThreadLocalRandom.current().nextInt(-sighSpread, sighSpread + 1) * index;
					
					Vector2 startPos = new Vector2(0, getHboxSize().x / 2 + WindupOffset).setAngle(startAngle);
					Vector2 startVeloCloud = new Vector2(0, cloudSpeed).setAngle(startAngle);
					Hitbox cloud = new Hitbox(state, getPixelPosition().add(startPos), windupSize, sighLifespan, new Vector2(), getHitboxfilter(), true, false, enemy, Sprite.NOTHING);

					cloud.addStrategy(new ControllerDefault(state, cloud, getBodyData()));
					cloud.addStrategy(new CreateParticles(state, cloud, getBodyData(), Particle.OVERCHARGE, 0.0f, particleLinger).setParticleColor(ParticleColor.BLUE).setParticleSize(60.0f));
					cloud.addStrategy(new HitboxStrategy(state, cloud, getBodyData()) {
						
						private float controllerCount;
						private boolean activated;
						@Override
						public void controller(float delta) {
							controllerCount += delta;
							
							if (controllerCount > cloudDelay) {
								
								if (!activated) {
									activated = true;
									SoundEffect.ICE_IMPACT.playUniversal(state, cloud.getPixelPosition(), 0.9f, 0.5f, false);
								}
								
								//after a delay, each cloud shoots a stream of ice outwards
								while (controllerCount >= cloudDelay + cloudInterval) {
									controllerCount -= cloudInterval;
								
									Hitbox hbox = new RangedHitbox(state, cloud.getPixelPosition(), cloudSize, cloudLifespan, startVeloCloud, getHitboxfilter(), true, false, enemy, Sprite.NOTHING);
									
									hbox.addStrategy(new ControllerDefault(state, hbox, getBodyData()));
									hbox.addStrategy(new CreateParticles(state, hbox, getBodyData(), Particle.ICE_CLOUD, 0.0f, particleLinger).setParticleSize(40.0f));
									hbox.addStrategy(new DamageStandard(state, hbox, getBodyData(), cloudDamage, cloudKB, DamageTypes.RANGED));
									hbox.addStrategy(new ContactWallDie(state, hbox, getBodyData()));
									hbox.addStrategy(new ContactUnitSlow(state, hbox, getBodyData(), slowDuration, slowSlow, Particle.ICE_CLOUD));
									hbox.addStrategy(new ContactUnitSound(state, hbox, getBodyData(), SoundEffect.DAMAGE3, 0.6f, true));
								}
							}
						}
					});
				}
			});
		}
		changeColor(ParticleColor.RED, 2.0f);
	}
	
	private static final float apocalypseWindup = 2.0f;
	
	private static final int apocalypseLaserNum = 100;
	private static final float apocalypseLaserInterval = 0.05f;
	private static final float apocalypseLaserSwivelSpeed = 0.6f;
	private static final float apocalypseLaserSpeed = 80.0f;
	private static final float apocalypseLaseramplitude = 4.0f;
	private static final float apocalypseLaserFrequency = 25.0f;

	private static final Sprite[] debrisSprites = {Sprite.SCRAP_A, Sprite.SCRAP_B, Sprite.SCRAP_C, Sprite.SCRAP_D};
	private static final float rubbleSpeed = 20.0f;
	private static final float rubbleLifespan = 5.0f;
	private static final Vector2 rubbleSize = new Vector2(40, 40);
	private static final float rubbleDamage = 8.0f;
	private static final float rubbleKB = 10.0f;
	private static final int rubbleSpread = 10;
	
	private void apocalypseLaser() {
		changeColor(ParticleColor.MIDNIGHT_BLUE, shot1Windup);
		singlePulse();
		singlePulseReturn();
		
		float startAngle = 240;
		
		Vector2 startVeloLaser = new Vector2(0, apocalypseLaserSpeed).setAngle(startAngle);
		Vector2 startPositionLaser = new Vector2();
		windupParticle(startAngle, Particle.CHARGING, ParticleColor.MIDNIGHT_BLUE, 30.0f, apocalypseWindup, apocalypseWindup);
		
		EnemyUtils.createSoundEntity(state, this, 0.0f, apocalypseLaserNum * apocalypseLaserInterval, 1.0f, 0.5f, SoundEffect.BEAM3, true);
		
		//boss fires a sweeping laser downwards consisting of a laser and 2 wave projectiles
		for (int i = 0; i < apocalypseLaserNum; i++) {
			getActions().add(new EnemyAction(this, apocalypseLaserInterval) {
				
				private final Vector2 laserOffset = new Vector2();
				@Override
				public void execute() {
					startVeloLaser.rotate(apocalypseLaserSwivelSpeed);
					startPositionLaser.set(getPixelPosition()).add(laserOffset.set(0, getHboxSize().x / 2 + WindupOffset).setAngle(startVeloLaser.angle()));
					Hitbox laser = new RangedHitbox(state, startPositionLaser, laserSize, trailLifespan, startVeloLaser, getHitboxfilter(), true, false, enemy, Sprite.LASER_BLUE);
					laser.setSpriteSize(laserSpriteSize);
					
					laser.addStrategy(new ControllerDefault(state, laser, getBodyData()));
					laser.addStrategy(new AdjustAngle(state, laser, getBodyData()));
					laser.addStrategy(new DieParticles(state, laser, getBodyData(), Particle.LASER_IMPACT).setParticleColor(ParticleColor.BLUE));
					laser.addStrategy(new ContactWallLoseDurability(state, laser, getBodyData()));
					laser.addStrategy(new DamageStandard(state, laser, getBodyData(), laserDamage, laserKB, DamageTypes.RANGED, DamageTypes.ENERGY));
					laser.addStrategy(new ContactUnitSound(state, laser, getBodyData(), SoundEffect.DAMAGE3, 0.6f, true));

					laser.addStrategy(new HitboxStrategy(state, laser, getBodyData()) {
						
						@Override
						public void create() {
							createWaveBeam(90);
							createWaveBeam(-90);
						}
						
						//each laser kicks up debris that flies upwards
						@Override
						public void die() {
							int randomIndex = GameStateManager.generator.nextInt(debrisSprites.length);
							Sprite projSprite = debrisSprites[randomIndex];
							Hitbox frag = new Hitbox(state, new Vector2(hbox.getPixelPosition()), rubbleSize, rubbleLifespan, new Vector2(0, rubbleSpeed), getHitboxfilter(), true, false, enemy, projSprite);
							frag.setGravity(1.0f);
							
							frag.addStrategy(new ControllerDefault(state, frag, getBodyData()));
							frag.addStrategy(new DamageStandard(state, frag, getBodyData(), rubbleDamage, rubbleKB, DamageTypes.RANGED));
							frag.addStrategy(new ContactWallDie(state, frag, getBodyData()));
							frag.addStrategy(new ContactWallParticles(state, frag, getBodyData(), Particle.SPARKS));
							frag.addStrategy(new ContactUnitSound(state, frag, getBodyData(), SoundEffect.DAMAGE3, 0.6f, true));
							frag.addStrategy(new Spread(state, frag, getBodyData(), rubbleSpread));
							frag.addStrategy(new ContactUnitSound(state, frag, getBodyData(), SoundEffect.DAMAGE3, 0.6f, true));
							frag.addStrategy(new ContactUnitDie(state, frag, getBodyData()));
							
						}
						
						private void createWaveBeam(float startAngle) {
							Hitbox hbox = new RangedHitbox(state, startPositionLaser, laserSize, trailLifespan, startVeloLaser, getHitboxfilter(), false, false, enemy, Sprite.LASER_BLUE);
							hbox.setSyncDefault(false);
							hbox.setSyncInstant(true);

							hbox.addStrategy(new ControllerDefault(state, hbox, getBodyData()));
							hbox.addStrategy(new ContactWallDie(state, hbox, getBodyData()));
							hbox.addStrategy(new ContactUnitLoseDurability(state, hbox, getBodyData()));
							hbox.addStrategy(new DamageStandard(state, hbox, getBodyData(), laserDamage, laserKB, DamageTypes.ENERGY, DamageTypes.RANGED));
							hbox.addStrategy(new ContactWallParticles(state, hbox, getBodyData(), Particle.LASER_IMPACT).setOffset(true).setParticleColor(ParticleColor.BLUE));
							hbox.addStrategy(new ContactUnitParticles(state, hbox, getBodyData(), Particle.LASER_IMPACT).setOffset(true).setParticleColor(ParticleColor.BLUE));
							hbox.addStrategy(new ContactUnitSound(state, hbox, getBodyData(), SoundEffect.DAMAGE3, 0.6f, true));
							hbox.addStrategy(new WaveEntity(state, hbox, getBodyData(), laser, apocalypseLaseramplitude, apocalypseLaserFrequency, startAngle));
						}
					});
				}
			});
		}
		
		changeColor(ParticleColor.RED, 0.0f);
	}
	
	private static final float horizontalBulletSpawnOffset = 100.0f;
	private static final int horizontalBulletNumber = 50;
	private static final float horizontalBulletInterval = 0.4f;
	private static final float horizontalBulletSpeed = 8.0f;
	private static final float horizontalBulletLifespan = 10.0f;
	private static final float horizontalBulletDamage = 10.0f;
	private static final float horizontalBulletKB = 15.0f;
	private static final float horizontalBulletWindDown = 10.0f;
	private static final Vector2 horizontalBulletSize = new Vector2(70, 35);
	private static final Vector2 horizontalBulletSpriteSize = new Vector2(100, 50);
	
	private void horizontalBullets() {
		changeColor(ParticleColor.PALE_GREEN, shot1Windup);
		singlePulse();
		singlePulseReturn();
		
		Vector2 bulletPosition = new Vector2();
		Vector2 bulletSpeed = new Vector2();
		
		//boss periodically summons horizontal moving bullets from the sides of the screen
		for (int i = 0; i < horizontalBulletNumber; i++) {
			getSecondaryActions().add(new EnemyAction(this, horizontalBulletInterval) {
				
				@Override
				public void execute() {
					bulletPosition.set(EnemyUtils.getLeftSide(state) - horizontalBulletSpawnOffset, ThreadLocalRandom.current().nextInt((int) EnemyUtils.floorHeight(state), (int) EnemyUtils.ceilingHeight(state)));
					bulletSpeed.set(horizontalBulletSpeed, 0);
					fireBullet(Sprite.LASER_TURQUOISE, ParticleColor.TURQOISE);
					
					bulletPosition.set(EnemyUtils.getRightSide(state) + horizontalBulletSpawnOffset, ThreadLocalRandom.current().nextInt((int) EnemyUtils.floorHeight(state), (int) EnemyUtils.ceilingHeight(state)));
					bulletSpeed.set(-horizontalBulletSpeed, 0);
					fireBullet(Sprite.LASER_GREEN, ParticleColor.PALE_GREEN);
				}
				
				private void fireBullet(Sprite sprite, ParticleColor color) {
					RangedHitbox hbox = new RangedHitbox(state, bulletPosition, horizontalBulletSize, horizontalBulletLifespan, bulletSpeed, getHitboxfilter(), true, false, enemy, sprite);
					hbox.setSpriteSize(horizontalBulletSpriteSize);
					
					hbox.addStrategy(new ControllerDefault(state, hbox, getBodyData()));
					hbox.addStrategy(new DamageStandard(state, hbox, getBodyData(), horizontalBulletDamage, horizontalBulletKB, DamageTypes.RANGED));
					hbox.addStrategy(new AdjustAngle(state, hbox, getBodyData()));
					hbox.addStrategy(new ContactUnitParticles(state, hbox, getBodyData(), Particle.LASER_IMPACT).setOffset(true).setParticleColor(color));
					hbox.addStrategy(new CreateParticles(state, hbox, getBodyData(), Particle.LASER_TRAIL, 0.0f, particleLinger).setParticleColor(color));
					hbox.addStrategy(new ContactUnitSound(state, hbox, getBodyData(), SoundEffect.DAMAGE3, 0.6f, true));
					hbox.addStrategy(new ContactUnitDie(state, hbox, getBodyData()));
				}
			});
		}
		changeColor(ParticleColor.RED, horizontalBulletWindDown);
	}
	
	private static final int numWillOWisp = 40;
	private static final float willOWispInterval = 0.1f;
	private static final float willOWispSpeed = 15.0f;
	private static final float willOWispLifespan = 10.0f;
	private static final float willOWispDamage = 11.0f;
	private static final float willOWispKB = 12.0f;
	private static final float willOWispHoming = 50.0f;
	private static final int willOWispSpread = 30;
	private static final Vector2 willOWispSize = new Vector2(25, 25);
	
	private void willOWisp() {
		changeColor(ParticleColor.VIOLET, shot1Windup);
		singlePulse();
		singlePulseReturn();
		
		//boss fires a large number of homing projectiles at the players
		for (int i = 0; i < numWillOWisp; i++) {
			getActions().add(new EnemyAction(this, willOWispInterval) {
				
				@Override
				public void execute() {
					SoundEffect.BOTTLE_ROCKET.playUniversal(state, getPixelPosition(), 0.25f, 0.5f, false);
					
					Vector2 startVelo = new Vector2(0, willOWispSpeed).setAngle(getAttackAngle());
					RangedHitbox hbox = new RangedHitbox(state, getProjectileOrigin(startVelo, willOWispSize.x), willOWispSize, willOWispLifespan, startVelo, getHitboxfilter(), true, true, enemy, Sprite.NOTHING);
					
					hbox.addStrategy(new ControllerDefault(state, hbox, getBodyData()));
					hbox.addStrategy(new DamageStandard(state, hbox, getBodyData(), willOWispDamage, willOWispKB, DamageTypes.RANGED));
					hbox.addStrategy(new ContactWallDie(state, hbox, getBodyData()));
					hbox.addStrategy(new CreateParticles(state, hbox, getBodyData(), Particle.BRIGHT, 0.0f, particleLinger).setParticleColor(ParticleColor.RANDOM));
					hbox.addStrategy(new ContactUnitDie(state, hbox, getBodyData()));
					hbox.addStrategy(new ContactUnitSound(state, hbox, getBodyData(), SoundEffect.DAMAGE3, 0.6f, true));
					hbox.addStrategy(new HomingUnit(state, hbox, getBodyData(), willOWispHoming, getHitboxfilter()));
					hbox.addStrategy(new Spread(state, hbox, getBodyData(), willOWispSpread));
				}
			});
		}
		changeColor(ParticleColor.RED, 0.0f);
	}
	
	private static final int numStar = 32;
	private static final float starInterval = 0.25f;
	
	private static final float starLifespan = 10.0f;
	private static final float starDamage = 16.0f;
	private static final float starKB = 18.0f;
	private static final int starSizeMin = 60;
	private static final int starSizeMax = 200;
	private static final int starSpeedMin = 10;
	private static final int starSpeedMax = 40;
	private static final int starDistMin = 10;
	private static final int starDistMax = 50;
	
	private static final Sprite[] starSprites = {Sprite.STAR_BLUE, Sprite.STAR_PURPLE, Sprite.STAR_RED, Sprite.STAR_YELLOW};
	private void orbitalStar() {
		changeColor(ParticleColor.GOLD, shot1Windup);
		singlePulse();
		singlePulseReturn();
		
		for (int i = 0; i < numStar; i++) {
			
			final int index = i;
			
			//boss summons a large number of stars that orbit its body
			getActions().add(new EnemyAction(this, starInterval) {
				
				@Override
				public void execute() {
					
					//stars vary in size, speed and distance from the boss
					float starSize = ThreadLocalRandom.current().nextInt(starSizeMin, starSizeMax);
					float starSpeed = ThreadLocalRandom.current().nextInt(starSpeedMin, starSpeedMax);
					float starDist = ThreadLocalRandom.current().nextInt(starDistMin, starDistMax);
					
					RangedHitbox hbox = new RangedHitbox(state, new Vector2(), new Vector2(starSize, starSize), starLifespan, new Vector2(), getHitboxfilter(), true, true, enemy, 
							starSprites[GameStateManager.generator.nextInt(starSprites.length)]);
					hbox.makeUnreflectable();
					hbox.setSpriteSize(new Vector2(starSize * 2.0f, starSize * 2.0f));
					
					hbox.addStrategy(new ControllerDefault(state, hbox, getBodyData()));
					hbox.addStrategy(new DamageStandard(state, hbox, getBodyData(), starDamage, starKB, DamageTypes.RANGED));
					hbox.addStrategy(new CreateParticles(state, hbox, getBodyData(), Particle.STAR, 0.0f, particleLinger).setParticleColor(ParticleColor.RANDOM));
					hbox.addStrategy(new ContactUnitSound(state, hbox, getBodyData(), SoundEffect.DAMAGE3, 0.6f, true));
					hbox.addStrategy(new ContactUnitDie(state, hbox, getBodyData()));
					
					if (index % 8 == 0) {
						new SoundEntity(state, hbox, SoundEffect.MAGIC25_SPELL, 0.8f, 0.5f, true, true, soundSyncType.TICKSYNC);
					}
					
					if (index % 2 == 0) {
						hbox.addStrategy(new OrbitUser(state, hbox, getBodyData(), 90, starDist, starSpeed));
					} else {
						hbox.addStrategy(new OrbitUser(state, hbox, getBodyData(), 90, starDist, -starSpeed));
					}
					
				}
			});
		}
		changeColor(ParticleColor.RED, 3.0f);
	}
	
	private static final float reticleInterval = 0.5f;
	private static final float reticleFollowDuration = 8.0f;
	private static final float reticleLifespan = 1.0f;
	private static final float reticleSize = 150;
	
	private static final int explosionRadius = 225;
	private static final float explosionDamage = 20.0f;
	private static final float explosionKnockback = 20.0f;
	
	private void reticleShots() {
		changeColor(ParticleColor.HOT_PINK, shot1Windup);
		singlePulse();
		singlePulseReturn();
		
		//boss repeatedly creates exploding reticles that follow the player
		getActions().add(new EnemyAction(this, 0.0f) {
			
			@Override
			public void execute() {
				applyHomingReticle(state.getPlayer());
				for (User user : HadalGame.server.getUsers().values()) {
					applyHomingReticle(user.getPlayer());
				}
			}
		});
		
		changeColor(ParticleColor.RED, 1.0f);
	}
	
	/**
	 * this creates a single exploding reticle over the input player's location
	 */
	private void applyHomingReticle(Player target) {
		if (target != null) {
			if (target.isAlive() && target.getPlayerData() != null) {
				
				target.getPlayerData().addStatus(new Status(state, reticleFollowDuration, false, getBodyData(), target.getPlayerData()) {
					
					private float controllerCount = reticleInterval;
					@Override
					public void timePassing(float delta) {
						super.timePassing(delta);
						
						controllerCount += delta;
						
						if (controllerCount > reticleInterval) {
							controllerCount -= reticleInterval;
							singleExplodingReticle(target.getPixelPosition());
						}
					}
				});
			}
		}
	}
	
	private static final int numReticleWaves = 15;
	private static final int reticleWavesAmount = 3;
	private static final float reticleWaveInterval = 0.2f;
	
	private void randomReticleWave() {
		singleVanish();
		changeColor(ParticleColor.RED, numReticleWaves * reticleWaveInterval);
		
		//the boss summons a large number of random exploding reticles
		for (int i = 0; i < numReticleWaves; i++) {
			getSecondaryActions().add(new EnemyAction(this, reticleWaveInterval) {
				
				@Override
				public void execute() {
					randomExplodingReticle();
				}
			});
		}
		
		singleReappear();
	}
	
	private final Vector2 reticleLocation = new Vector2();
	private void randomExplodingReticle() {
		
		for (int i = 0; i < reticleWavesAmount; i++) {
			reticleLocation.set(
					ThreadLocalRandom.current().nextInt((int) EnemyUtils.getLeftSide(state), (int) EnemyUtils.getRightSide(state)), 
					ThreadLocalRandom.current().nextInt((int) EnemyUtils.floorHeight(state), (int) EnemyUtils.ceilingHeight(state)));
			
			singleExplodingReticle(reticleLocation);
		}
	}
	
	private void singleExplodingReticle(Vector2 position) {
		WeaponUtils.createExplodingReticle(state, position, this, reticleSize, reticleLifespan, explosionDamage, explosionKnockback, explosionRadius);
	}
	
	private static final float teleportDuration = 3.0f;
	
	//when transitioning from phase 1 to 2, the boss fades, moves off screen, then reappears at the top of the screen
	private void teleport() {
		singleVanish();
		
		getActions().add(new EnemyAction(this, teleportDuration) {
			
			@Override
			public void execute() {
				setTransform(new Vector2(-100, -100), body.getAngle());
			}
		});
		
		getActions().add(new EnemyAction(this, 0.0f) {
			
			@Override
			public void execute() {
				Event ceiling = state.getDummyPoint("ceiling");
				
				if (ceiling != null) {
					setTransform(new Vector2(ceiling.getPosition()), body.getAngle());
				}
			}
		});
		
		singleReappear();
	}
	
	private static final float shrinkDurationIn = 1.0f;
	private static final float shrinkLerpIn = 0.08f;
	private static final float shrinkScaleIn = 0.1f;
	
	private static final float shrinkDurationOut = 0.2f;
	private static final float shrinkLerpOut = 0.2f;
	private static final float shrinkScaleOut = 1.5f;
	
	private static final float shrinkDurationReturn = 1.0f;
	private static final float shrinkLerpReturn = 0.04f;
	private static final float shrinkScaleReturn = 1.0f;

	private static final float vanishDuration = 2.0f;
	private static final float vanishLerp = 0.04f;
	private static final float vanishScale = 0.0f;

	private static final float reappearDuration = 1.0f;
	private static final float reappearLerp = 0.1f;
	private static final float reappearScale = 1.0f;
	
	//these methods all make the boss shrink/grow with different intervals
	private void singlePulse() {
		scaleSize(shrinkScaleIn, shrinkLerpIn, shrinkDurationIn);
		scaleSize(shrinkScaleOut, shrinkLerpOut, shrinkDurationOut);
	}
	
	private void singlePulseReturn() {
		scaleSize(shrinkScaleReturn, shrinkLerpReturn, shrinkDurationReturn);
	}
	
	private void singleVanish() {
		scaleSize(vanishScale, vanishLerp, vanishDuration);
	}
	
	private void singleReappear() {
		scaleSize(reappearScale, reappearLerp, reappearDuration);
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
	
	//this creates particles along the boss' perimeter.
	private static final float WindupOffset = 15.0f;
	private void windupParticle(float startAngle, Particle particle, ParticleColor color, float particleScale, float lifespan, float duration) {
		
		getActions().add(new EnemyAction(this, duration) {
			
			private final Vector2 addVector = new Vector2();
			@Override
			public void execute() {
				Vector2 startVelo1 = new Vector2(0, getHboxSize().x / 2 + WindupOffset).setAngle(startAngle);
				Hitbox hbox1 = new Hitbox(state, getPixelPosition().add(startVelo1), windupSize, lifespan, new Vector2(), getHitboxfilter(), true, false, enemy, Sprite.NOTHING);

				hbox1.addStrategy(new ControllerDefault(state, hbox1, getBodyData()));
				hbox1.addStrategy(new CreateParticles(state, hbox1, getBodyData(), particle, 0.0f, particleLinger).setParticleColor(color).setParticleSize(particleScale));
				hbox1.addStrategy(new HitboxStrategy(state, hbox1, getBodyData()) {
					
					@Override
					public void controller(float delta) {
						if (!enemy.isAlive()) {
							hbox.die();
						} else {
							hbox.setTransform(addVector.set(getPixelPosition()).add(startVelo1).scl(1.0f / 32), 0);
						}
					}
				});
			}
		});
	}
}
