package com.mygdx.hadal.schmucks.bodies.enemies;

import static com.mygdx.hadal.utils.Constants.PPM;

import java.util.concurrent.ThreadLocalRandom;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.BossUtils;
import com.mygdx.hadal.event.SpawnerSchmuck;
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.states.PlayState;

/**
 * Enemies are Schmucks that attack the player.
 * Floating enemies are the basic fish-enemies of the game
 * @author Zachary Tu
 *
 */
public class Boss1 extends BossFloating {
				
    private static final float aiAttackCd = 3.0f;
    private static final float aiAttackCd2 = 2.2f;
    private static final float aiAttackCd3 = 1.5f;
	
	private static final int width = 250;
	private static final int height = 161;
	
	private static final int hbWidth = 161;
	private static final int hbHeight = 250;
	
	private static final float scale = 1.0f;
	
	private static final int hp = 4200;
	private static final int moveSpeed = 20;
	private static final int spinSpeed = 40;
	
	private static final Sprite sprite = Sprite.FISH_TORPEDO;
	
	private int phase = 1;
	private static final float phaseThreshold2 = 0.70f;
	private static final float phaseThreshold3 = 0.35f;
	
	/**
	 * Enemy constructor is run when an enemy spawner makes a new enemy.
	 * @param state: current gameState
	 * @param world: box2d world
	 * @param camera: game camera
	 * @param rays: game rayhandler
	 * @param width: width of enemy
	 * @param height: height of enemy
	 * @param x: enemy starting x position.
	 * @param y: enemy starting x position.
	 */
	public Boss1(PlayState state, int x, int y, enemyType type, short filter, SpawnerSchmuck spawner) {
		super(state, x, y, width, height, hbWidth, hbHeight, scale, type, filter, hp, moveSpeed, spinSpeed, aiAttackCd, spawner, sprite);
	}

	private int attackNum = 0;
	@Override
	public void attackInitiate() {
		
		attackNum++;
		
		if (phase == 1) {
			if (bodyData.getCurrentHp() <= phaseThreshold2 * bodyData.getMaxHp()) {
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
			if (bodyData.getCurrentHp() <= phaseThreshold3 * bodyData.getMaxHp()) {
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
	private static final int charge1Damage = 20;
	private static final int charge2Damage = 8;
	private static final int defaultMeleeKB = 50;
	
	private void chargeAttack1() {
		BossUtils.moveToRandomCorner(state, this, moveSpeed);
		BossUtils.changeTrackingState(this, BossState.SPINNING, spinSpeed, 1.2f);
		BossUtils.changeTrackingState(this, BossState.TRACKING_PLAYER, 0, 0.4f);
		BossUtils.moveToPlayer(state, this, target, charge1Speed, 0.0f);
		BossUtils.meleeAttack(state, this, charge1Damage,defaultMeleeKB, target, 1.5f);
	}
	
	private void chargeAttack2() {
		int corner = BossUtils.moveToRandomCorner(state, this, moveSpeed);
		BossUtils.changeTrackingState(this, BossState.SPINNING, spinSpeed, 0.5f);
		BossUtils.meleeAttack(state, this, charge2Damage, defaultMeleeKB, target, 2.75f);
		switch (corner) {
		case 0:
			BossUtils.moveToDummy(state, this, "2", charge2Speed);
			BossUtils.moveToDummy(state, this, "8", charge2Speed);
			BossUtils.moveToDummy(state, this, "6", charge2Speed);
			BossUtils.moveToDummy(state, this, "0", charge2Speed);
			break;
		case 1:
			BossUtils.moveToDummy(state, this, "8", charge2Speed);
			BossUtils.moveToDummy(state, this, "6", charge2Speed);
			BossUtils.moveToDummy(state, this, "0", charge2Speed);
			BossUtils.moveToDummy(state, this, "2", charge2Speed);
			break;
		case 2:
			BossUtils.moveToDummy(state, this, "0", charge2Speed);
			BossUtils.moveToDummy(state, this, "2", charge2Speed);
			BossUtils.moveToDummy(state, this, "8", charge2Speed);
			BossUtils.moveToDummy(state, this, "6", charge2Speed);
			break;
		case 3:
			BossUtils.moveToDummy(state, this, "6", charge2Speed);
			BossUtils.moveToDummy(state, this, "0", charge2Speed);
			BossUtils.moveToDummy(state, this, "2", charge2Speed);
			BossUtils.moveToDummy(state, this, "8", charge2Speed);
			break;
		}
		BossUtils.changeTrackingState(this, BossState.TRACKING_PLAYER, 0, 0.0f);
	}
	
	private static final int fireballDamage = 3;
	private static final int burnDamage = 2;
	private static final int fireSpeed = 12;
	private static final int fireKB = 10;
	private static final int fireSize = 50;
	private static final float fireLifespan = 1.5f;
	private static final float burnDuration = 4.0f;

	private static final int fireballNumber = 40;
	private static final float fireballInterval = 0.02f;
	
	private void fireBreath() {
		int wall = BossUtils.moveToRandomWall(state, this, moveSpeed);
		BossUtils.changeTrackingState(this, BossState.FREE, -90.0f, 1.5f);
		switch (wall) {
		case 0 :
			BossUtils.changeTrackingState(this, BossState.FREE, 0.0f, 0.0f);
			for (int i = 0; i < fireballNumber; i++) {
				BossUtils.fireball(state, this, fireballDamage, burnDamage, fireSpeed, fireKB, fireSize, 0, fireLifespan, burnDuration, fireballInterval);
			}
			break;
		case 1: 
			BossUtils.changeTrackingState(this, BossState.FREE, -180.0f, 0.0f);
			for (int i = 0; i < fireballNumber; i++) {
				BossUtils.fireball(state, this, fireballDamage, burnDamage, fireSpeed, fireKB, fireSize, 0, fireLifespan, burnDuration, fireballInterval);
			}
			break;
		}
		BossUtils.changeTrackingState(this, BossState.TRACKING_PLAYER, 0, 0.0f);
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
		int wall = BossUtils.moveToRandomWall(state, this, moveSpeed);
		switch (wall) {
		case 0 :
			
			BossUtils.changeTrackingState(this, BossState.FREE, 0.0f, 0.0f);
			for (int i = 0; i < trackAmount; i++) {
				BossUtils.trackPlayerXY(state, this, target, trackSpeed, trackInterval, false);
			}
			BossUtils.stopStill(this, 0.2f);
			for (int i = 0; i < laser1Amount; i++) {
				BossUtils.fireLaser(state, this, laser1Damage, laser1Speed, laserKnockback, laserSize, laserLifespan, laser1Interval, Particle.LASER);
			}
			break;
		case 1: 
			BossUtils.changeTrackingState(this, BossState.FREE, -180.0f, 0.0f);
			for (int i = 0; i < trackAmount; i++) {
				BossUtils.trackPlayerXY(state, this, target, trackSpeed, trackInterval, false);
			}
			BossUtils.stopStill(this, 0.2f);
			for (int i = 0; i < laser1Amount; i++) {
				BossUtils.fireLaser(state, this, laser1Damage, laser1Speed, laserKnockback, laserSize, laserLifespan, laser1Interval, Particle.LASER);
			}
			break;
		}
		BossUtils.changeTrackingState(this, BossState.TRACKING_PLAYER, 0, 0.0f);
	}
	
	private static final float rotateSpeed = 1.8f;
	private static final float laser2Interval = 0.04f;
	private static final int laser2Amount = 110;
	private static final float laser2Damage = 4.0f;
	private static final float laser2Speed = 15.0f;
	
	private void rotatingLaser() {
		BossUtils.moveToDummy(state, this, "4", moveSpeed);
		BossUtils.changeTrackingState(this, BossState.FREE, -90.0f, 2.0f);
		
		boolean random = GameStateManager.generator.nextBoolean();
		
		if (random) {
			BossUtils.changeTrackingState(this, BossState.SPINNING, rotateSpeed, 0.0f);
		} else {
			BossUtils.changeTrackingState(this, BossState.SPINNING, -rotateSpeed, 0.0f);
		}
		
		for (int i = 0; i < laser2Amount; i++) {
			BossUtils.fireLaser(state, this, laser2Damage, laser2Speed, laserKnockback, laserSize, laserLifespan, laser2Interval, Particle.LASER_PULSE);
		}
		BossUtils.changeTrackingState(this, BossState.TRACKING_PLAYER, 0, 2.0f);
	}
	
	private static final int laser3Amount = 40;
	private static final float laser3Damage = 7.5f;
	private static final float laser3Knockback = 1.0f;
	private static final float laser3Speed = 55.0f;
	private static final int explosionNumber = 4;
	private static final float explosionDamage = 35.0f;
	private static final float explosionKnockback = 35.0f;
	private static final int explosionRadius = 500;
	private static final float explosionInterval = 0.25f;
	
	private void sweepingLaser() {
		int wall = BossUtils.moveToRandomWall(state, this, moveSpeed);
		BossUtils.changeTrackingState(this, BossState.FREE, -90.0f, 1.0f);
		
		switch (wall) {
		case 0 :
			BossUtils.changeTrackingState(this, BossState.FREE, -15.0f, 0.0f);
			for (int i = 0; i < laser3Amount; i++) {
				BossUtils.fireLaser(state, this, laser3Damage, laser3Speed, laser3Knockback, laserSize, laserLifespan, laser2Interval, Particle.LASER_PULSE);
			}
			for (int i = 1; i <= explosionNumber; i++) {
				BossUtils.createExplosion(state, this, explosionDamage, explosionKnockback, explosionRadius, new Vector2(BossUtils.getLeftSide(state) + i * explosionRadius / 2,
						BossUtils.floorHeight(state)), explosionInterval);
			}
			break;
		case 1: 
			BossUtils.changeTrackingState(this, BossState.FREE, -165.0f, 0.0f);
			for (int i = 0; i < laser3Amount; i++) {
				BossUtils.fireLaser(state, this, laser3Damage, laser3Speed, laserKnockback, laserSize, laserLifespan, laser2Interval, Particle.LASER_PULSE);
			}
			for (int i = 1; i <= explosionNumber; i++) {
				BossUtils.createExplosion(state, this, explosionDamage, explosionKnockback, explosionRadius, new Vector2(BossUtils.getRightSide(state) - i * explosionRadius / 2,
						BossUtils.floorHeight(state)), explosionInterval);
			}
			break;
		}
		BossUtils.changeTrackingState(this, BossState.TRACKING_PLAYER, 0, 0.0f);
	}
	
	private static final int numBalls = 3;
	private static final int spread = 15;
	private static final float ballDamage = 12.0f;
	private static final float ballSpeed = 10.0f;
	private static final float ballKnockback = 12.0f;
	private static final int ballSize = 120;
	private static final float ballLifespan = 7.0f;
	private static final float ballInterval= 0.75f;
	
	private void bouncyBall() {
		boolean random = GameStateManager.generator.nextBoolean();
		float baseAngle = 0;
		if (random) {
			BossUtils.moveToDummy(state, this, "0", moveSpeed);
			baseAngle = -60.0f;
		} else {
			BossUtils.moveToDummy(state, this, "2", moveSpeed);
			baseAngle = -120.0f;
		}
		
		for (int i = 0; i < numBalls; i++) {
			BossUtils.changeTrackingState(this, BossState.FREE, baseAngle + (ThreadLocalRandom.current().nextInt(-spread, spread + 1)), ballInterval);
			BossUtils.bouncingBall(state, this, ballDamage, ballSpeed, ballKnockback, ballSize, ballLifespan, 0.0f);
		}
		BossUtils.changeTrackingState(this, BossState.TRACKING_PLAYER, 0, 2.0f);
	}
	
	
	private static final float spiritDamage= 13.0f;
	private static final float spiritKnockback= 25.0f;
	private static final float spiritLifespan= 7.0f;
	private Vector2 spiritPos = new Vector2();
	private void vengefulSpirit() {
		BossUtils.changeTrackingState(this, BossState.SPINNING, spinSpeed, 0.75f);
		BossUtils.changeTrackingState(this, BossState.TRACKING_PLAYER, 0, 0.0f);
		
		spiritPos.set(body.getPosition()).scl(PPM).add(0, 150);
		BossUtils.vengefulSpirit(state, this, spiritDamage, spiritKnockback, spiritLifespan, new Vector2(spiritPos), 0.0f);
		
		spiritPos.set(body.getPosition()).scl(PPM).add(150, 0);
		BossUtils.vengefulSpirit(state, this, spiritDamage, spiritKnockback, spiritLifespan, new Vector2(spiritPos), 0.0f);
		
		spiritPos.set(body.getPosition()).scl(PPM).add(-150, 0);
		BossUtils.vengefulSpirit(state, this, spiritDamage, spiritKnockback, spiritLifespan, new Vector2(spiritPos), 0.0f);
	}
	
	private static final int numPoison = 7;
	private static final float poisonInterval = 0.75f;
	private static final float poisonDamage= 0.5f;
	private static final int poisonWidth= 150;
	private static final int poisonHeight = 280;
	private static final float poisonDuration = 7.0f;
	
	private void poisonCloud() {
		int wall = BossUtils.moveToRandomWall(state, this, moveSpeed);
		if (wall == 0) {
			BossUtils.changeTrackingState(this, BossState.FREE, -75.0f, 0.5f);
			for (int i = 0; i < numPoison; i++) {
				BossUtils.createPoison(state, this, poisonWidth, poisonHeight, poisonDamage, poisonDuration, new Vector2(BossUtils.getLeftSide(state) + i * poisonWidth, 
						BossUtils.floorHeight(state) + poisonHeight / 2), poisonInterval);
			}
		} else {
			BossUtils.changeTrackingState(this, BossState.FREE, -105.0f, 0.5f);
			for (int i = 0; i < numPoison; i++) {
				BossUtils.createPoison(state, this, poisonWidth, poisonHeight, poisonDamage, poisonDuration, new Vector2(BossUtils.getRightSide(state) - i * poisonWidth, 
						BossUtils.floorHeight(state) + poisonHeight / 2), poisonInterval);
			}
		}
		BossUtils.changeTrackingState(this, BossState.TRACKING_PLAYER, 0, 0.0f);
	}
	
	private static final int numAdds = 3;
	private void spawnAdds() {
		BossUtils.moveToDummy(state, this, "4", moveSpeed);
		BossUtils.changeTrackingState(this, BossState.SPINNING, spinSpeed, 0.75f);
		BossUtils.changeTrackingState(this, BossState.TRACKING_PLAYER, 0, 0.0f);
		for (int i = 0; i < numAdds; i++) {
			BossUtils.spawnAdds(state, this, enemyType.TORPEDOFISH, 1, 1.5f);
		}
		BossUtils.changeTrackingState(this, BossState.TRACKING_PLAYER, 0, 2.0f);
	}
	
	private static final int numDebris = 30;
	private static final int numDebrisPassive = 10;
	private static final float debrisInterval = 0.25f;
	private static final float debrisDamage= 7.0f;
	private static final int debrisSize= 60;
	private static final float debrisKnockback= 15.0f;
	private static final float debrisLifespan= 3.0f;
	private void fallingDebris() {
		BossUtils.changeTrackingState(this, BossState.SPINNING, spinSpeed, 1.0f);
		for (int i = 0; i < numDebris; i++) {
			BossUtils.fallingDebris(state, this, debrisDamage, debrisSize, debrisKnockback, debrisLifespan, debrisInterval);
		}
		BossUtils.changeTrackingState(this, BossState.TRACKING_PLAYER, 0, 0.0f);
	}
	
	private void fallingDebrisPassive() {
		for (int i = 0; i < numDebrisPassive; i++) {
			BossUtils.fallingDebris(state, this, debrisDamage, debrisSize, debrisKnockback, debrisLifespan, debrisInterval);
		}
	}
}