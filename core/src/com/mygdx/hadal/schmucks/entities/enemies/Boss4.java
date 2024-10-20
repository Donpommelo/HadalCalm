package com.mygdx.hadal.schmucks.entities.enemies;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.utils.IntArray;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.EnemyUtils;
import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.constants.Stats;
import com.mygdx.hadal.constants.SyncType;
import com.mygdx.hadal.effects.HadalColor;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.schmucks.entities.ParticleEntity;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.users.User;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.strategies.HitboxStrategy;
import com.mygdx.hadal.strategies.enemy.CreateMultiplayerHpScaling;
import com.mygdx.hadal.strategies.hitbox.ControllerDefault;
import com.mygdx.hadal.strategies.hitbox.CreateParticles;

/**
 * This is a boss in the game
 * @author Futubbery Frinnifer
 */
public class Boss4 extends EnemyFloating {
	
    private static final float aiAttackCd = 2.2f;
    private static final float aiAttackCd2 = 1.6f;
	
    private static final int scrapDrop = 15;
    
	private static final int width = 360;
	private static final int height = 360;
	
	private static final int hbWidth = 360;
	private static final int hbHeight = 360;
	
	private static final float scale = 1.0f;
	
	private static final int hp = 10000;
	
	private static final Sprite sprite = Sprite.NOTHING;
	
	//these control the scaling of the boss' body particles
	private float currentScale = 1.0f;
	private float desiredScale = 1.0f;
	private float scaleLerpFactor = 0.2f;
		
	private int phase = 1;
	private static final float phaseThreshold2 = 0.5f;
	
	//the boss's body is composed of multiple scaled up particle effects
	private ParticleEntity body1, body2, body3;
	private static final float bodyBaseScale1 = 2.5f;
	private static final float bodyBaseScale2 = 2.5f;
	private static final float bodyBaseScale3 = 5.0f;
	
	public Boss4(PlayState state, Vector2 startPos, short filter) {
		super(state, startPos, new Vector2(width, height).scl(scale), new Vector2(hbWidth, hbHeight).scl(scale), sprite, EnemyType.BOSS4, filter, hp, aiAttackCd, scrapDrop);
		addStrategy(new CreateMultiplayerHpScaling(state, this, 2000));

		body1 = new ParticleEntity(state, this, Particle.WORMHOLE, 1.0f, 0.0f, true, SyncType.NOSYNC);
		body1.setScale(bodyBaseScale1).setColor(HadalColor.RED);
		body2 = new ParticleEntity(state, this, Particle.STORM, 1.0f, 0.0f, true, SyncType.NOSYNC);
		body2.setScale(bodyBaseScale2).setColor(HadalColor.ORANGE);
		body3 = new ParticleEntity(state, this, Particle.BRIGHT, 1.0f, 0.0f, true, SyncType.NOSYNC);
		body3.setScale(bodyBaseScale3).setColor(HadalColor.RED);
	}
	
	
	private static final float charge1Damage = 1.5f;
	private static final float attackInterval = 0.1f;
	private static final int defaultMeleeKB = 3;
	@Override
	public void create() {
		super.create();

		body.setType(BodyType.KinematicBody);
		
		getBodyData().addStatus(new StatChangeStatus(state, Stats.KNOCKBACK_RES, 1.0f, getBodyData()));
	}

	private float scalingAccumulator;
	private static final float scalingTime = 1 / 120f;
	@Override
	public void controller(float delta) {
		super.controller(delta);

		if (state.isServer()) {
			body1.getEffect().update(delta);
			body2.getEffect().update(delta);
			body3.getEffect().update(delta);

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
	}
	
	@Override
	public void render(SpriteBatch batch, Vector2 entityLocation) {
		if (state.isServer()) {
			body1.getEffect().draw(batch);
			body2.getEffect().draw(batch);
			body3.getEffect().draw(batch);
		}
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
	private final IntArray attacks1 = new IntArray();
	private final IntArray attacks2 = new IntArray();
	private void phase1Attack() {
		EnemyUtils.meleeAttackContinuous(state, this, charge1Damage, attackInterval, defaultMeleeKB, aiAttackCd);

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
			int nextAttack = attacks1.removeIndex(MathUtils.random(attacks1.size - 1));
			switch (nextAttack) {
				case 0 -> reticleShots();
				case 1 -> deadStarSigh();
				case 2 -> bigBangBell();
			}
		} else {
			int nextAttack = attacks2.removeIndex(MathUtils.random(attacks2.size - 1));
			switch (nextAttack) {
				case 0 -> radialShot1();
				case 1 -> twinFlameSpin();
				case 2 -> bounceLaser();
			}
		}
	}
	
	
	private void phase2Attack() {
		EnemyUtils.meleeAttackContinuous(state, this, charge1Damage, attackInterval, defaultMeleeKB, aiAttackCd2);

		if (attacks1.isEmpty()) {
			for (int i = 0; i < phase2NumAttacks; i++) {
				attacks1.add(i);
			}
		}
		
		int nextAttack = attacks1.removeIndex(MathUtils.random(attacks1.size - 1));
		switch (nextAttack) {
			case 0 -> apocalypseLaser();
			case 1 -> horizontalBullets();
			case 2 -> orbitalStar();
			case 3 -> randomReticleWave();
			case 4 -> willOWisp();
		}
	}
	
	private static final float particleLinger = 1.0f;
	private static final float shot1Windup = 1.5f;
	private void radialShot1() {
		changeColor(HadalColor.VIOLET, shot1Windup);
		singlePulse();
		
		getActions().add(new EnemyAction(this, 0.0f) {
			
			@Override
			public void execute() {
				SyncedAttack.FALSE_SUN_RADIAL.initiateSyncedAttackMulti(state, enemy, new Vector2(), new Vector2[] {}, new Vector2[] {});
			}
		});
		
		singlePulseReturn();
		changeColor(HadalColor.RED, 0.0f);
	}
	
	private static final float fireWindup = 0.5f;

	private static final int fireballNumber = 80;
	private static final float fireballInterval = 0.075f;
	private static final int fireSpeed = 10;

	private static final Vector2 windupSize = new Vector2(120, 120);
	
	private void twinFlameSpin() {
		changeColor(HadalColor.ORANGE, shot1Windup);
		singlePulse();
		singlePulseReturn();

		final float startAngle = getAttackAngle();
		
		windupParticle(startAngle, Particle.FIRE, HadalColor.NOTHING, 40.0f, fireWindup, 0.0f);
		windupParticle(startAngle + 180, Particle.FIRE, HadalColor.NOTHING, 40.0f, fireWindup, fireWindup);
		EnemyUtils.createSoundEntity(state, this, 0.0f, fireballNumber * fireballInterval, 0.6f, 2.0f, SoundEffect.FLAMETHROWER, true);
		for (int i = 0; i < fireballNumber; i++) {
			
			final int index = i;
			
			getActions().add(new EnemyAction(this, fireballInterval) {
				
				@Override
				public void execute() {
					
					Vector2 startVelo1 = new Vector2(fireSpeed, fireSpeed).setAngleDeg(startAngle + index * 360.0f / fireballNumber);
					SyncedAttack.FALSE_SUN_FIRE_SPIN.initiateSyncedAttackSingle(state, enemy, enemy.getPixelPosition(), startVelo1);

					Vector2 startVelo2 = new Vector2(fireSpeed, fireSpeed).setAngleDeg(startAngle + index * 360.0f / fireballNumber + 180);
					SyncedAttack.FALSE_SUN_FIRE_SPIN.initiateSyncedAttackSingle(state, enemy, enemy.getPixelPosition(), startVelo2);
				}
			});
		}
		
		changeColor(HadalColor.RED, 0.0f);
	}
	
	private void bigBangBell() {
		changeColor(HadalColor.GOLDEN_YELLOW, shot1Windup);
		singlePulse();
		
		getActions().add(new EnemyAction(this, 0.0f) {
			
			@Override
			public void execute() {
				SyncedAttack.FALSE_SUN_BELL.initiateSyncedAttackSingle(state, enemy, enemy.getPixelPosition(), new Vector2());
			}
		});
		
		singlePulseReturn();
		changeColor(HadalColor.RED, 0.0f);
	}
	
	private static final int laserSpread = 4;
	
	private static final int trailNumber = 5;
	private static final float trailInterval = 0.5f;
	
	private static final float trailSpeed = 200.0f;

	private static final int laserNumber = 40;
	private static final float laserInterval = 0.05f;
	
	private static final float laserSpeed = 125.0f;

	private static final int[] startingVelos = {30, 60, 120, 150, 210, 240, 300, 330};
	
	private void bounceLaser() {
		changeColor(HadalColor.BLUE, shot1Windup);
		
		final float startAngle = startingVelos[MathUtils.random(startingVelos.length - 1)] +
				MathUtils.random(-laserSpread, laserSpread + 1);
		
		Vector2 startVeloTrail = new Vector2(0, trailSpeed).setAngleDeg(startAngle);
		Vector2 startPosLaser = new Vector2(getPixelPosition()).add(new Vector2(0, getHboxSize().x / 2 + WindupOffset).setAngleDeg(startAngle));
		
		//the boss first sends out a series of trails that mark a path
		for (int i = 0; i < trailNumber; i++) {
			getActions().add(new EnemyAction(this, trailInterval) {
				
				@Override
				public void execute() {
					SyncedAttack.FALSE_SUN_LASER_TRAIL.initiateSyncedAttackSingle(state, enemy, startPosLaser, startVeloTrail);
				}
			});
		}
		
		windupParticle(startAngle, Particle.OVERCHARGE, HadalColor.BLUE, 30.0f, laserNumber * laserInterval, 0.0f);
		
		Vector2 startVeloLaser = new Vector2(0, laserSpeed).setAngleDeg(startAngle);
		
		EnemyUtils.createSoundEntity(state, this, 0.0f, laserNumber * laserInterval, 1.0f, 2.0f, SoundEffect.BEAM3, true);
		
		//next, the boss fires a laser along the marked path
		for (int i = 0; i < laserNumber; i++) {
			getActions().add(new EnemyAction(this, laserInterval) {
				
				@Override
				public void execute() {
					SyncedAttack.FALSE_SUN_LASER.initiateSyncedAttackSingle(state, enemy, startPosLaser, startVeloLaser);
				}
			});
		}
		
		changeColor(HadalColor.RED, 0.0f);
	}
	
	private static final int sighNumber = 5;
	private static final int sighSpread = 60;
	private static final float sighInterval = 0.4f;
	private static final float cloudSpeed = 60.0f;

	private void deadStarSigh() {
		changeColor(HadalColor.TURQUOISE, shot1Windup);
		singlePulse();
		singlePulseReturn();
		
		for (int i = 0; i < sighNumber; i++) {
			
			final int index = i;
			
			//the boss first creates a number of clouds along its perimeter
			getActions().add(new EnemyAction(this, sighInterval) {
				
				@Override
				public void execute() {
					float startAngle = getAttackAngle() + MathUtils.random(-sighSpread, sighSpread + 1) * index;
					Vector2 startPos = new Vector2(0, getHboxSize().x / 2 + WindupOffset).setAngleDeg(startAngle);
					Vector2 startVeloCloud = new Vector2(0, cloudSpeed).setAngleDeg(startAngle);
					SyncedAttack.FALSE_SUN_SIGH.initiateSyncedAttackSingle(state, enemy, getPixelPosition().add(startPos), startVeloCloud);
				}
			});
		}
		changeColor(HadalColor.RED, 2.0f);
	}
	
	private static final float apocalypseWindup = 2.0f;
	
	private static final int apocalypseLaserNum = 100;
	private static final float apocalypseLaserInterval = 0.05f;
	private static final float apocalypseLaserSwivelSpeed = 0.6f;
	private static final float apocalypseLaserSpeed = 80.0f;

	private void apocalypseLaser() {
		changeColor(HadalColor.MIDNIGHT_BLUE, shot1Windup);
		singlePulse();
		singlePulseReturn();
		
		float startAngle = 240;
		
		Vector2 startVeloLaser = new Vector2(0, apocalypseLaserSpeed).setAngleDeg(startAngle);
		Vector2 startPositionLaser = new Vector2();
		windupParticle(startAngle, Particle.CHARGING, HadalColor.MIDNIGHT_BLUE, 30.0f, apocalypseWindup, apocalypseWindup);
		
		EnemyUtils.createSoundEntity(state, this, 0.0f, apocalypseLaserNum * apocalypseLaserInterval, 1.0f, 0.5f, SoundEffect.BEAM3, true);
		
		//boss fires a sweeping laser downwards consisting of a laser and 2 wave projectiles
		for (int i = 0; i < apocalypseLaserNum; i++) {
			getActions().add(new EnemyAction(this, apocalypseLaserInterval) {
				
				private final Vector2 laserOffset = new Vector2();
				@Override
				public void execute() {
					startVeloLaser.rotateDeg(apocalypseLaserSwivelSpeed);
					startPositionLaser.set(getPixelPosition()).add(laserOffset.set(0, getHboxSize().x / 2 + WindupOffset).setAngleDeg(startVeloLaser.angleDeg()));
					SyncedAttack.FALSE_SUN_APOCALYPSE.initiateSyncedAttackSingle(state, enemy, startPositionLaser, startVeloLaser);
				}
			});
		}
		changeColor(HadalColor.RED, 0.0f);
	}
	
	private static final float horizontalBulletSpawnOffset = 100.0f;
	private static final int horizontalBulletNumber = 50;
	private static final float horizontalBulletInterval = 0.4f;
	private static final float horizontalBulletSpeed = 8.0f;
	private static final float horizontalBulletWindDown = 10.0f;

	private void horizontalBullets() {
		changeColor(HadalColor.PALE_GREEN, shot1Windup);
		singlePulse();
		singlePulseReturn();
		
		Vector2 bulletPosition = new Vector2();
		Vector2 bulletSpeed = new Vector2();
		
		//boss periodically summons horizontal moving bullets from the sides of the screen
		for (int i = 0; i < horizontalBulletNumber; i++) {
			getSecondaryActions().add(new EnemyAction(this, horizontalBulletInterval) {
				
				@Override
				public void execute() {
					bulletPosition.set(EnemyUtils.getLeftSide(state) - horizontalBulletSpawnOffset,
							MathUtils.random((int) EnemyUtils.floorHeight(state), (int) EnemyUtils.ceilingHeight(state)));
					bulletSpeed.set(horizontalBulletSpeed, 0);
					SyncedAttack.FALSE_SUN_BULLETS.initiateSyncedAttackSingle(state, enemy, bulletPosition, bulletSpeed);

					bulletPosition.set(EnemyUtils.getRightSide(state) + horizontalBulletSpawnOffset,
							MathUtils.random((int) EnemyUtils.floorHeight(state), (int) EnemyUtils.ceilingHeight(state)));
					bulletSpeed.set(-horizontalBulletSpeed, 0);
					SyncedAttack.FALSE_SUN_BULLETS.initiateSyncedAttackSingle(state, enemy, bulletPosition, bulletSpeed);
				}
			});
		}
		changeColor(HadalColor.RED, horizontalBulletWindDown);
	}
	
	private static final int numWillOWisp = 40;
	private static final float willOWispInterval = 0.1f;
	private static final float willOWispSpeed = 15.0f;

	private void willOWisp() {
		changeColor(HadalColor.VIOLET, shot1Windup);
		singlePulse();
		singlePulseReturn();
		
		//boss fires a large number of homing projectiles at the players
		for (int i = 0; i < numWillOWisp; i++) {
			getActions().add(new EnemyAction(this, willOWispInterval) {
				
				@Override
				public void execute() {
					Vector2 startVelo = new Vector2(0, willOWispSpeed).setAngleDeg(getAttackAngle());
					SyncedAttack.FALSE_SUN_WILL_O_WISP.initiateSyncedAttackSingle(state, enemy, getPixelPosition(), startVelo);
				}
			});
		}
		changeColor(HadalColor.RED, 0.0f);
	}
	
	private static final int numStar = 32;
	private static final float starInterval = 0.25f;
	
	private static final int starSizeMin = 60;
	private static final int starSizeMax = 200;
	private static final int starSpeedMin = 10;
	private static final int starSpeedMax = 40;
	private static final int starDistMin = 10;
	private static final int starDistMax = 50;
	
	private void orbitalStar() {
		changeColor(HadalColor.GOLDEN_YELLOW, shot1Windup);
		singlePulse();
		singlePulseReturn();
		
		for (int i = 0; i < numStar; i++) {
			
			final int index = i;
			
			//boss summons a large number of stars that orbit its body
			getActions().add(new EnemyAction(this, starInterval) {
				
				@Override
				public void execute() {
					
					//stars vary in size, speed and distance from the boss
					float starSize = MathUtils.random(starSizeMin, starSizeMax);
					float starSpeed = MathUtils.random(starSpeedMin, starSpeedMax);
					float starDist = MathUtils.random(starDistMin, starDistMax);

					SyncedAttack.FALSE_SUN_STAR_ORBIT.initiateSyncedAttackSingle(state, enemy, getPixelPosition(), new Vector2(),
							index, starSize, starSpeed, starDist);
				}
			});
		}
		changeColor(HadalColor.RED, 3.0f);
	}
	
	private static final float reticleInterval = 0.5f;
	private static final float reticleFollowDuration = 8.0f;

	private void reticleShots() {
		changeColor(HadalColor.HOT_PINK, shot1Windup);
		singlePulse();
		singlePulseReturn();
		
		//boss repeatedly creates exploding reticles that follow the player
		getActions().add(new EnemyAction(this, 0.0f) {
			
			@Override
			public void execute() {
				for (User user : HadalGame.usm.getUsers().values()) {
					applyHomingReticle(user.getPlayer());
				}
			}
		});
		
		changeColor(HadalColor.RED, 1.0f);
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
		changeColor(HadalColor.RED, numReticleWaves * reticleWaveInterval);
		
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
					MathUtils.random(EnemyUtils.getLeftSide(state), EnemyUtils.getRightSide(state)),
					MathUtils.random(EnemyUtils.floorHeight(state), EnemyUtils.ceilingHeight(state)));
			
			singleExplodingReticle(reticleLocation);
		}
	}
	
	private void singleExplodingReticle(Vector2 position) {
		SyncedAttack.FALSE_SUN_RETICLE.initiateSyncedAttackSingle(state, this, position, new Vector2());
	}
	
	private static final float teleportDuration = 3.0f;
	
	//when transitioning from phase 1 to 2, the boss fades, moves off screen, then reappears at the top of the screen
	private void teleport() {
		singleVanish();
		
		getActions().add(new EnemyAction(this, teleportDuration) {
			
			@Override
			public void execute() {
				setTransform(new Vector2(-100, -100), getAngle());
			}
		});
		
		getActions().add(new EnemyAction(this, 0.0f) {
			
			@Override
			public void execute() {
				Event ceiling = state.getDummyPoint("ceiling");
				
				if (ceiling != null) {
					setTransform(new Vector2(ceiling.getPosition()), getAngle());
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
	
	private void changeColor(HadalColor color, float duration) {
		getActions().add(new EnemyAction(this, duration) {
			
			@Override
			public void execute() {
				body3.setColor(color);
			}
		});
	}
	
	//this creates particles along the boss' perimeter.
	private static final float WindupOffset = 15.0f;
	private void windupParticle(float startAngle, Particle particle, HadalColor color, float particleScale, float lifespan, float duration) {
		
		getActions().add(new EnemyAction(this, duration) {
			
			private final Vector2 addVector = new Vector2();
			@Override
			public void execute() {
				Vector2 startVelo1 = new Vector2(0, getHboxSize().x / 2 + WindupOffset).setAngleDeg(startAngle);
				Hitbox hbox1 = new Hitbox(state, getPixelPosition().add(startVelo1), windupSize, lifespan, new Vector2(), getHitboxFilter(), true, false, enemy, Sprite.NOTHING);
				hbox1.setSynced(true);
				hbox1.setSyncedDelete(true);

				hbox1.addStrategy(new ControllerDefault(state, hbox1, getBodyData()));
				hbox1.addStrategy(new CreateParticles(state, hbox1, getBodyData(), particle, 0.0f, particleLinger)
						.setParticleColor(color).setParticleSize(particleScale));
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
