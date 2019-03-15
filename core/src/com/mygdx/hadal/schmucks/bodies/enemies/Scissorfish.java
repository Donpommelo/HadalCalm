package com.mygdx.hadal.schmucks.bodies.enemies;

import com.mygdx.hadal.equip.enemy.ScissorfishAttack;
import com.mygdx.hadal.states.PlayState;

public class Scissorfish extends FloatingEnemy {

	private static final int width = 288;
	private static final int height = 119;
	
	private static final int hbWidth = 119;
	private static final int hbHeight = 288;
	
	private static final float scale = 0.25f;
	
	private static final float maxLinearSpeed = 50;
	private static final float maxLinearAcceleration = 1000;
	private static final float maxAngularSpeed = 1080;
	private static final float maxAngularAcceleration = 720;
	
	private static final int boundingRadius = 500;
	private static final int decelerationRadius = 0;
	
	private static final String spriteId = "scissorfish_swim";
	
	public Scissorfish(PlayState state, int x, int y) {
		super(state, x, y, width, height, hbWidth, hbHeight, scale, spriteId, enemyType.SCISSORFISH,
				maxLinearSpeed, maxLinearAcceleration, maxAngularSpeed, maxAngularAcceleration, 
				boundingRadius, decelerationRadius);
		
		this.weapon = new ScissorfishAttack(this);	
	}
}
