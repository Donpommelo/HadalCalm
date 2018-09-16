package com.mygdx.hadal.schmucks.bodies.enemies;

import com.mygdx.hadal.equip.enemy.SpittlefishAttack;
import com.mygdx.hadal.states.PlayState;

public class Spittlefish extends FloatingEnemy {

	private static final int width = 197;
	private static final int height = 76;
	
	private static final int hbWidth = 76;
	private static final int hbHeight = 197;
	
	private static final float scale = 0.25f;
	
	private static final float maxLinearSpeed = 10;
	private static final float maxLinearAcceleration = 200;
	private static final float maxAngularSpeed = 3240;
	private static final float maxAngularAcceleration = 2160;
	
	private static final int boundingRadius = 500;
	private static final int decelerationRadius = 100;
	
	private static final String spriteId = "spittlefish_swim";

	public Spittlefish(PlayState state, int x, int y) {
		super(state, x, y, width, height, hbWidth, hbHeight, scale, spriteId,
				maxLinearSpeed, maxLinearAcceleration, maxAngularSpeed, maxAngularAcceleration, 
				boundingRadius, decelerationRadius);
		
		this.weapon = new SpittlefishAttack(this);	
	}

}
