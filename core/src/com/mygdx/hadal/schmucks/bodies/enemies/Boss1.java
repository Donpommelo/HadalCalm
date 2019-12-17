package com.mygdx.hadal.schmucks.bodies.enemies;

import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.BossUtils;
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.states.PlayState;

/**
 * Enemies are Schmucks that attack the player.
 * Floating enemies are the basic fish-enemies of the game
 * @author Zachary Tu
 *
 */
public class Boss1 extends BossFloating {
				
    private static final float aiAttackCd = 2.0f;
	
	private static final int width = 250;
	private static final int height = 161;
	
	private static final int hbWidth = 161;
	private static final int hbHeight = 250;
	
	private static final float scale = 1.0f;
	
	private static final int hp = 2000;
	private static final int moveSpeed = 20;
	private static final int spinSpeed = 40;
	
	private static final Sprite sprite = Sprite.FISH_TORPEDO;
	
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
	public Boss1(PlayState state, int x, int y, enemyType type, short filter) {
		super(state, x, y, width, height, hbWidth, hbHeight, scale, type, filter, hp, moveSpeed, spinSpeed, aiAttackCd, sprite);
	}

	@Override
	public void attackInitiate() {
		
		int randomIndex = GameStateManager.generator.nextInt(4);
		
		switch(randomIndex) {
		case 0: 
			chargeAttack1();
			break;
		case 1: 
			chargeAttack2();
			break;
		case 2: 
			spawnAdds();
			break;
		case 3: 
			fireBreath();
			break;
		}
	}
	
	private static final int charge1Speed = 40;
	private static final int charge2Speed = 30;
	private static final int defaultMeleeDamage = 12;
	private static final int defaultMeleeKB = 50;
	
	private void chargeAttack1() {
		BossUtils.moveToRandomCorner(state, this, moveSpeed);
		BossUtils.changeTrackingState(this, BossState.SPINNING, spinSpeed, 0.75f);
		BossUtils.changeTrackingState(this, BossState.TRACKING_PLAYER, 0, 0.0f);
		BossUtils.moveToPlayer(state, this, target, charge1Speed, 0.0f);
		BossUtils.meleeAttack(state, this, defaultMeleeDamage,defaultMeleeKB, target, 1.5f);
	}
	
	private void chargeAttack2() {
		int corner = BossUtils.moveToRandomCorner(state, this, moveSpeed);
		BossUtils.changeTrackingState(this, BossState.SPINNING, spinSpeed, 0.2f);
		BossUtils.meleeAttack(state, this, defaultMeleeDamage, defaultMeleeKB, target, 2.25f);
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
	
	private static final int numAdds = 3;
	private void spawnAdds() {
		BossUtils.moveToDummy(state, this, "4", moveSpeed);
		BossUtils.changeTrackingState(this, BossState.SPINNING, spinSpeed, 0.75f);
		BossUtils.changeTrackingState(this, BossState.TRACKING_PLAYER, 0, 0.0f);
		for (int i = 0; i < numAdds; i++) {
			BossUtils.spawnAdds(state, this, enemyType.TORPEDOFISH, 1, 1.5f);
		}
	}
	
	private static final int fireballDamage = 2;
	private static final int burnDamage = 1;
	private static final int fireSpeed = 12;
	private static final int fireKB = 10;
	private static final int fireSize = 50;
	private static final float fireLifespan = 1.5f;
	private static final float burnDuration = 4.0f;

	private static final int fireballNumber = 30;
	private static final float fireballInterval = 0.75f;
	
	private void fireBreath() {
		int wall = BossUtils.moveToRandomWall(state, this, moveSpeed);
		
		switch (wall) {
		case 0 :
			BossUtils.changeTrackingState(this, BossState.FREE, -90.0f, 1.0f);
			BossUtils.changeTrackingState(this, BossState.FREE, 90.0f, 0.0f);
			for (int i = 0; i < fireballNumber; i++) {
				BossUtils.fireball(state, this, fireballDamage, burnDamage, fireSpeed, fireKB, fireSize, 0, fireLifespan, burnDuration, fireballInterval);
			}
			break;
		case 1: 
			BossUtils.changeTrackingState(this, BossState.FREE, -90.0f, 1.0f);
			BossUtils.changeTrackingState(this, BossState.FREE, -270.0f, 0.0f);
			for (int i = 0; i < fireballNumber; i++) {
				BossUtils.fireball(state, this, fireballDamage, burnDamage, fireSpeed, fireKB, fireSize, 0, fireLifespan, burnDuration, fireballInterval);
			}
		}
		BossUtils.changeTrackingState(this, BossState.TRACKING_PLAYER, 0, 0.0f);
	}

	public enum BossAttack {
		SPAWN_ADDS,
		CHARGE1,
		CHARGE2,
		FIRE_SPIN
	}
}