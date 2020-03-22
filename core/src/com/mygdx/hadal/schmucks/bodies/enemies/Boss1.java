package com.mygdx.hadal.schmucks.bodies.enemies;

import java.util.concurrent.ThreadLocalRandom;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.EnemyUtils;
import com.mygdx.hadal.equip.WeaponUtils;
import com.mygdx.hadal.event.SpawnerSchmuck;
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.utils.Stats;

/**
 * This is a boss in the game
 * @author Zachary Tu
 *
 */
public class Boss1 extends EnemyFloating {
	
	private final static String name = "BOSS1";

    private static final float aiAttackCd = 3.0f;
    private static final float aiAttackCd2 = 2.2f;
    private static final float aiAttackCd3 = 1.5f;
	
    private final static int scrapDrop = 15;
    
	private static final int width = 250;
	private static final int height = 161;
	
	private static final int hbWidth = 250;
	private static final int hbHeight = 161;
	
	private static final float scale = 1.0f;
	
	private static final int hp = 4500;
	private static final int moveSpeed = 20;
	private static final int spinSpeed = 40;
	
	private static final Sprite sprite = Sprite.FISH_TORPEDO;
	
	private int phase = 1;
	private static final float phaseThreshold2 = 0.70f;
	private static final float phaseThreshold3 = 0.35f;
	
	public Boss1(PlayState state, Vector2 startPos, short filter, SpawnerSchmuck spawner) {
		super(state, startPos, new Vector2(width, height).scl(scale), new Vector2(hbWidth, hbHeight).scl(scale), name, sprite, EnemyType.BOSS1, filter, hp, aiAttackCd, scrapDrop, spawner);
	}
	
	@Override
	public void create() {
		super.create();
		getBodyData().addStatus(new StatChangeStatus(state, Stats.KNOCKBACK_RES, 1.0f, getBodyData()));
	}

	private int attackNum = 0;
	@Override
	public void attackInitiate() {
		attackNum++;
		if (phase == 1) {
			if (getBodyData().getCurrentHp() <= phaseThreshold2 * getBodyData().getStat(Stats.MAX_HP)) {
				phase = 2;
				setAttackCd(aiAttackCd2);
				spawnAdds();
			} else {
				int randomIndex = GameStateManager.generator.nextInt(4);
				switch(randomIndex) {
				case 0: 
					chargeAttack1();
					break;
				case 1: 
					chargeAttack2();
					break;
				case 2: 
					fireBreath();
					break;
				case 3: 
					fallingDebris();
					break;
				}
			}
		}
		
		if (phase == 2) {
			if (getBodyData().getCurrentHp() <= phaseThreshold3 * getBodyData().getStat(Stats.MAX_HP)) {
				phase = 3;
				setAttackCd(aiAttackCd3);
				spawnAdds();
			} else if (attackNum % 2 == 0) {
				int randomIndex = GameStateManager.generator.nextInt(5);
				switch(randomIndex) {
				case 0: 
					chargeAttack1();
					break;
				case 1: 
					chargeAttack2();
					break;
				case 2: 
					fireBreath();
					break;
				case 3: 
					horizontalLaser();
					break;
				case 4: 
					sweepingLaser();
					break;
				}
			} else {
				int randomIndex = GameStateManager.generator.nextInt(4);
				switch(randomIndex) {
				case 0: 
					bouncyBall();
					break;
				case 1: 
					vengefulSpirit();
					break;
				case 2: 
					poisonCloud();
					break;
				case 3: 
					fallingDebris();
					break;
				}
			}
		}
		
		if (phase == 3) {
			if (attackNum % 2 == 0) {
				int randomIndex = GameStateManager.generator.nextInt(6);
				switch(randomIndex) {
				case 0: 
					chargeAttack1();
					break;
				case 1: 
					chargeAttack2();
					break;
				case 2: 
					fireBreath();
					break;
				case 3: 
					sweepingLaser();
					break;
				case 4: 
					horizontalLaser();
					break;
				case 5:
					rotatingLaser();
					break;
				}
			} else {
				int randomIndex = GameStateManager.generator.nextInt(4);
				switch(randomIndex) {
				case 0: 
					bouncyBall();
					break;
				case 1: 
					vengefulSpirit();
					break;
				case 2: 
					fallingDebris();
					break;
				case 3: 
					poisonCloud();
					break;
				}
			}
			fallingDebrisPassive();
		}
	}
	
	private static final int charge1Speed = 35;
	private static final int charge2Speed = 25;
	private static final float charge1Damage = 2.5f;
	private static final float charge2Damage = 1.5f;
	private static final int charge1Knockback = 12;
	private static final int charge2Knockback = 12;

	private static final float moveDurationMax = 5.0f;
	
	private static final float chargeAttackInterval = 1 / 60.0f;
	private static final float charge1AttackDuration = 1.25f;
	private static final float charge2AttacksDuration = 2.75f;
	
	private void chargeAttack1() {
		EnemyUtils.moveToRandomCorner(state, this, moveSpeed, moveDurationMax);
		EnemyUtils.changeFloatingState(this, FloatingState.SPINNING, spinSpeed, 1.2f);
		EnemyUtils.changeFloatingState(this, FloatingState.TRACKING_PLAYER, 0, 0.4f);
		EnemyUtils.moveToPlayer(state, this, target, charge1Speed, 0.0f);
		EnemyUtils.meleeAttackContinuous(state, this, charge1Damage, chargeAttackInterval, charge1Knockback, charge1AttackDuration);
	}
	
	private void chargeAttack2() {
		int corner = EnemyUtils.moveToRandomCorner(state, this, moveSpeed, moveDurationMax);
		EnemyUtils.changeFloatingState(this, FloatingState.SPINNING, spinSpeed, 0.5f);
		EnemyUtils.meleeAttackContinuous(state, this, charge2Damage, chargeAttackInterval, charge2Knockback, charge2AttacksDuration);
		
		switch (corner) {
		case 0:
			EnemyUtils.moveToDummy(state, this, "2", charge2Speed, moveDurationMax);
			EnemyUtils.moveToDummy(state, this, "8", charge2Speed, moveDurationMax);
			EnemyUtils.moveToDummy(state, this, "6", charge2Speed, moveDurationMax);
			EnemyUtils.moveToDummy(state, this, "0", charge2Speed, moveDurationMax);
			break;
		case 1:
			EnemyUtils.moveToDummy(state, this, "8", charge2Speed, moveDurationMax);
			EnemyUtils.moveToDummy(state, this, "6", charge2Speed, moveDurationMax);
			EnemyUtils.moveToDummy(state, this, "0", charge2Speed, moveDurationMax);
			EnemyUtils.moveToDummy(state, this, "2", charge2Speed, moveDurationMax);
			break;
		case 2:
			EnemyUtils.moveToDummy(state, this, "0", charge2Speed, moveDurationMax);
			EnemyUtils.moveToDummy(state, this, "2", charge2Speed, moveDurationMax);
			EnemyUtils.moveToDummy(state, this, "8", charge2Speed, moveDurationMax);
			EnemyUtils.moveToDummy(state, this, "6", charge2Speed, moveDurationMax);
			break;
		case 3:
			EnemyUtils.moveToDummy(state, this, "6", charge2Speed, moveDurationMax);
			EnemyUtils.moveToDummy(state, this, "0", charge2Speed, moveDurationMax);
			EnemyUtils.moveToDummy(state, this, "2", charge2Speed, moveDurationMax);
			EnemyUtils.moveToDummy(state, this, "8", charge2Speed, moveDurationMax);
			break;
		}
		EnemyUtils.changeFloatingState(this, FloatingState.TRACKING_PLAYER, 0, 0.0f);
	}
	
	private static final int fireballDamage = 4;
	private static final int burnDamage = 3;
	private static final int fireSpeed = 11;
	private static final int fireKB = 10;
	private static final int fireSize = 50;
	private static final float fireLifespan = 1.5f;
	private static final float burnDuration = 4.0f;

	private static final int fireballNumber = 40;
	private static final float fireballInterval = 0.02f;
	
	private void fireBreath() {
		int wall = EnemyUtils.moveToRandomWall(state, this, moveSpeed, moveDurationMax);
		EnemyUtils.changeFloatingState(this, FloatingState.FREE, -90.0f, 1.5f);
		switch (wall) {
		case 0 :
			EnemyUtils.changeFloatingState(this, FloatingState.FREE, 0.0f, 0.0f);
			for (int i = 0; i < fireballNumber; i++) {
				EnemyUtils.fireball(state, this, fireballDamage, burnDamage, fireSpeed, fireKB, fireSize, fireLifespan, burnDuration, fireballInterval);
			}
			break;
		case 1: 
			EnemyUtils.changeFloatingState(this, FloatingState.FREE, -180.0f, 0.0f);
			for (int i = 0; i < fireballNumber; i++) {
				EnemyUtils.fireball(state, this, fireballDamage, burnDamage, fireSpeed, fireKB, fireSize, fireLifespan, burnDuration, fireballInterval);
			}
			break;
		}
		EnemyUtils.changeFloatingState(this, FloatingState.TRACKING_PLAYER, 0, 0.0f);
	}
	
	private static final float trackInterval = 0.25f;
	private static final int trackAmount = 12;
	private static final int trackSpeed = 60;
	
	private static final float laser1Interval = 0.03f;
	private static final int laser1Amount = 40;
	private static final float laser1Damage = 6.0f;
	private static final float laserKnockback = 5.0f;
	private static final float laser1Speed = 55.0f;
	private static final int laserSize = 30;
	private static final float laserLifespan = 1.2f;
	
	private void horizontalLaser() {
		int wall = EnemyUtils.moveToRandomWall(state, this, moveSpeed, moveDurationMax);
		switch (wall) {
		case 0 :
			
			EnemyUtils.changeFloatingState(this, FloatingState.FREE, 0.0f, 0.0f);
			for (int i = 0; i < trackAmount; i++) {
				EnemyUtils.trackPlayerXY(state, this, target, trackSpeed, trackInterval, false);
			}
			EnemyUtils.stopStill(this, 0.2f);
			for (int i = 0; i < laser1Amount; i++) {
				EnemyUtils.fireLaser(state, this, laser1Damage, laser1Speed, laserKnockback, laserSize, laserLifespan, laser1Interval, Particle.LASER);
			}
			break;
		case 1: 
			EnemyUtils.changeFloatingState(this, FloatingState.FREE, -180.0f, 0.0f);
			for (int i = 0; i < trackAmount; i++) {
				EnemyUtils.trackPlayerXY(state, this, target, trackSpeed, trackInterval, false);
			}
			EnemyUtils.stopStill(this, 0.2f);
			for (int i = 0; i < laser1Amount; i++) {
				EnemyUtils.fireLaser(state, this, laser1Damage, laser1Speed, laserKnockback, laserSize, laserLifespan, laser1Interval, Particle.LASER);
			}
			break;
		}
		EnemyUtils.changeFloatingState(this, FloatingState.TRACKING_PLAYER, 0, 0.0f);
	}
	
	private static final float rotateSpeed = 2.5f;
	private static final float laser2Interval = 0.02f;
	private static final int laser2Amount = 100;
	private static final float laser2Damage = 7.5f;
	private static final float laser2Speed = 15.0f;
	
	private void rotatingLaser() {
		EnemyUtils.moveToDummy(state, this, "4", moveSpeed, moveDurationMax);
		EnemyUtils.changeFloatingState(this, FloatingState.FREE, -90.0f, 2.0f);
		
		boolean random = GameStateManager.generator.nextBoolean();
		
		if (random) {
			EnemyUtils.changeFloatingState(this, FloatingState.ROTATING, rotateSpeed, 0.0f);
		} else {
			EnemyUtils.changeFloatingState(this, FloatingState.ROTATING, -rotateSpeed, 0.0f);
		}
		
		for (int i = 0; i < laser2Amount; i++) {
			EnemyUtils.fireLaser(state, this, laser2Damage, laser2Speed, laserKnockback, laserSize, laserLifespan, laser2Interval, Particle.LASER_PULSE);
		}
		EnemyUtils.changeFloatingState(this, FloatingState.TRACKING_PLAYER, 0, 1.0f);
	}
	
	private static final int laser3Amount = 25;
	private static final float laser3Damage = 3.5f;
	private static final float laser3Knockback = 1.0f;
	private static final float laser3Speed = 55.0f;
	private static final int explosionNumber = 6;
	private static final float explosionDamage = 35.0f;
	private static final float explosionKnockback = 35.0f;
	private static final float explosionSize = 300;
	private static final float explosionInterval = 0.25f;
	
	private void sweepingLaser() {
		int wall = EnemyUtils.moveToRandomWall(state, this, moveSpeed, moveDurationMax);
		EnemyUtils.changeFloatingState(this, FloatingState.FREE, -90.0f, 1.0f);
		
		switch (wall) {
		case 0 :
			EnemyUtils.changeFloatingState(this, FloatingState.FREE, -15.0f, 0.0f);
			for (int i = 0; i < laser3Amount; i++) {
				EnemyUtils.fireLaser(state, this, laser3Damage, laser3Speed, laser3Knockback, laserSize, laserLifespan, laser2Interval, Particle.LASER_PULSE);
			}
			for (int i = 1; i <= explosionNumber; i++) {
				EnemyUtils.createExplosion(state, this, new Vector2(EnemyUtils.getLeftSide(state) + i * explosionSize / 2,
						EnemyUtils.floorHeight(state)), explosionSize, explosionDamage, explosionKnockback, explosionInterval);
			}
			break;
		case 1: 
			EnemyUtils.changeFloatingState(this, FloatingState.FREE, -165.0f, 0.0f);
			for (int i = 0; i < laser3Amount; i++) {
				EnemyUtils.fireLaser(state, this, laser3Damage, laser3Speed, laserKnockback, laserSize, laserLifespan, laser2Interval, Particle.LASER_PULSE);
			}
			for (int i = 1; i <= explosionNumber; i++) {
				EnemyUtils.createExplosion(state, this, new Vector2(EnemyUtils.getRightSide(state) - i * explosionSize / 2,
						EnemyUtils.floorHeight(state)), explosionSize, explosionDamage, explosionKnockback, explosionInterval);
			}
			break;
		}
		EnemyUtils.changeFloatingState(this, FloatingState.TRACKING_PLAYER, 0, 0.0f);
	}
	
	private static final int numBalls = 3;
	private static final int spread = 15;
	private static final float ballDamage = 12.0f;
	private static final float ballSpeed = 10.0f;
	private static final float ballKnockback = 12.0f;
	private static final int ballSize = 60;
	private static final float ballLifespan = 7.5f;
	private static final float ballInterval= 1.0f;
	
	private void bouncyBall() {
		boolean random = GameStateManager.generator.nextBoolean();
		float baseAngle = 0;
		if (random) {
			EnemyUtils.moveToDummy(state, this, "0", moveSpeed, moveDurationMax);
			baseAngle = -60.0f;
		} else {
			EnemyUtils.moveToDummy(state, this, "2", moveSpeed, moveDurationMax);
			baseAngle = -120.0f;
		}
		
		for (int i = 0; i < numBalls; i++) {
			EnemyUtils.changeFloatingState(this, FloatingState.FREE, baseAngle + (ThreadLocalRandom.current().nextInt(-spread, spread + 1)), ballInterval);
			EnemyUtils.bouncingBall(state, this, ballDamage, ballSpeed, ballKnockback, ballSize, ballLifespan, 0.0f);
		}
		EnemyUtils.changeFloatingState(this, FloatingState.TRACKING_PLAYER, 0, 2.0f);
	}
	
	
	private static final float spiritDamage= 15.0f;
	private static final float spiritKnockback= 25.0f;
	private static final float spiritLifespan= 5.0f;
	private Vector2 spiritPos = new Vector2();
	private void vengefulSpirit() {
		EnemyUtils.changeFloatingState(this, FloatingState.SPINNING, spinSpeed, 0.75f);
		EnemyUtils.changeFloatingState(this, FloatingState.TRACKING_PLAYER, 0, 0.0f);
		
		getActions().add(new EnemyAction(this, 0.0f) {
			
			@Override
			public void execute() {
				spiritPos.set(getPixelPosition()).add(0, 100);
				WeaponUtils.releaseVengefulSpirits(state, new Vector2(spiritPos), spiritLifespan, spiritDamage, spiritKnockback, enemy.getBodyData(), enemy.getHitboxfilter());
				spiritPos.set(getPixelPosition()).add(100, 0);
				WeaponUtils.releaseVengefulSpirits(state, new Vector2(spiritPos), spiritLifespan, spiritDamage, spiritKnockback, enemy.getBodyData(), enemy.getHitboxfilter());
				spiritPos.set(getPixelPosition()).add(-100, 0);
				WeaponUtils.releaseVengefulSpirits(state, new Vector2(spiritPos), spiritLifespan, spiritDamage, spiritKnockback, enemy.getBodyData(), enemy.getHitboxfilter());
			}
		});
	}
	
	private static final int numPoison = 7;
	private static final float poisonInterval = 0.75f;
	private static final float poisonDamage= 0.6f;
	private static final int poisonWidth= 150;
	private static final int poisonHeight = 280;
	private static final float poisonDuration = 6.0f;
	
	private void poisonCloud() {
		int wall = EnemyUtils.moveToRandomWall(state, this, moveSpeed, moveDurationMax);
		if (wall == 0) {
			EnemyUtils.changeFloatingState(this, FloatingState.FREE, -75.0f, 2.5f);
			for (int i = 0; i < numPoison; i++) {
				EnemyUtils.createPoison(state, this, new Vector2(EnemyUtils.getLeftSide(state) + i * poisonWidth, EnemyUtils.floorHeight(state) + poisonHeight / 2),
						new Vector2(poisonWidth, poisonHeight), poisonDamage, poisonDuration, poisonInterval);
			}
		} else {
			EnemyUtils.changeFloatingState(this, FloatingState.FREE, -105.0f, 2.5f);
			for (int i = 0; i < numPoison; i++) {
				EnemyUtils.createPoison(state, this, new Vector2(EnemyUtils.getRightSide(state) - i * poisonWidth, EnemyUtils.floorHeight(state) + poisonHeight / 2),
						new Vector2(poisonWidth, poisonHeight), poisonDamage, poisonDuration, poisonInterval);
			}
		}
		EnemyUtils.changeFloatingState(this, FloatingState.TRACKING_PLAYER, 0, 0.0f);
	}
	
	private static final int numAdds = 3;
	private void spawnAdds() {
		EnemyUtils.moveToDummy(state, this, "4", moveSpeed, moveDurationMax);
		EnemyUtils.changeFloatingState(this, FloatingState.SPINNING, spinSpeed, 0.75f);
		EnemyUtils.changeFloatingState(this, FloatingState.TRACKING_PLAYER, 0, 0.0f);
		for (int i = 0; i < numAdds; i++) {
			EnemyUtils.spawnAdds(state, this, EnemyType.TORPEDOFISH, 1, 0.0f, 1.5f);
		}
		EnemyUtils.changeFloatingState(this, FloatingState.TRACKING_PLAYER, 0, 2.0f);
	}
	
	private static final int numDebris = 18;
	private static final int numDebrisPassive = 8;
	private static final float debrisInterval = 0.25f;
	private static final float debrisDamage= 9.0f;
	private static final int debrisSize= 30;
	private static final float debrisKnockback= 15.0f;
	private static final float debrisLifespan= 3.0f;
	private void fallingDebris() {
		EnemyUtils.changeFloatingState(this, FloatingState.SPINNING, spinSpeed, 1.0f);
		for (int i = 0; i < numDebris; i++) {
			EnemyUtils.fallingDebris(state, this, debrisDamage, debrisSize, debrisKnockback, debrisLifespan, debrisInterval);
		}
		EnemyUtils.changeFloatingState(this, FloatingState.TRACKING_PLAYER, 0, 0.0f);
	}
	
	private void fallingDebrisPassive() {
		for (int i = 0; i < numDebrisPassive; i++) {
			EnemyUtils.fallingDebris(state, this, debrisDamage, debrisSize, debrisKnockback, debrisLifespan, debrisInterval);
		}
	}
}
