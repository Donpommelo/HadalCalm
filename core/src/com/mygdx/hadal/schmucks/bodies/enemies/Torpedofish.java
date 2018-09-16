package com.mygdx.hadal.schmucks.bodies.enemies;

import com.mygdx.hadal.equip.enemy.TorpedofishAttack;
import com.mygdx.hadal.states.PlayState;

public class Torpedofish extends FloatingEnemy {

	private static final int width = 250;
	private static final int height = 161;
	
	private static final int hbWidth = 161;
	private static final int hbHeight = 250;
	
	private static final float scale = 0.25f;
	
	private static final float maxLinearSpeed = 25;
	private static final float maxLinearAcceleration = 800;
	private static final float maxAngularSpeed = 4320;
	private static final float maxAngularAcceleration = 3240;
	
	private static final int boundingRadius = 500;
	private static final int decelerationRadius = 100;
	
	private static final String spriteId = "torpedofish_swim";

	public Torpedofish(PlayState state, int x, int y) {
		super(state, x, y, width, height, hbWidth, hbHeight, scale, spriteId,
				maxLinearSpeed, maxLinearAcceleration, maxAngularSpeed, maxAngularAcceleration, 
				boundingRadius, decelerationRadius);
		
		this.weapon = new TorpedofishAttack(this);	
	}
}
