package com.mygdx.hadal.schmucks.bodies.enemies;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.equip.enemy.ScissorfishAttack;
import com.mygdx.hadal.states.PlayState;

import box2dLight.RayHandler;

public class Scissorfish extends FloatingEnemy {

	private static final int width = 288;
	private static final int height = 119;
	
	private static final int hbWidth = 119;
	private static final int hbHeight = 288;
	
	private static final float scale = 0.25f;
	
	private static final float maxLinearSpeed = 200;
	private static final float maxLinearAcceleration = 1000;
	private static final float maxAngularSpeed = 10;
	private static final float maxAngularAcceleration = 5;
	
	private static final int boundingRadius = 500;
	private static final int decelerationRadius = 0;
	
	private static final String spriteId = "scissorfish_swim";
	
	public Scissorfish(PlayState state, World world, OrthographicCamera camera, RayHandler rays, int x, int y) {
		super(state, world, camera, rays, x, y, width, height, hbWidth, hbHeight, scale, spriteId,
				maxLinearSpeed, maxLinearAcceleration, maxAngularSpeed, maxAngularAcceleration, boundingRadius, decelerationRadius);
		
		this.weapon = new ScissorfishAttack(this);	
	}

}
