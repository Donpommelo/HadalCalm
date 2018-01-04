package com.mygdx.hadal.schmucks.bodies.enemies;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.equip.enemy.ScissorfishAttack;
import com.mygdx.hadal.states.PlayState;

import box2dLight.RayHandler;

public class Scissorfish extends FloatingEnemy {

	public static final int width = 288;
	public static final int height = 119;
	
	public static final int hbWidth = 119;
	public static final int hbHeight = 288;
	
	public static final float scale = 0.25f;
	
	public static final float maxLinearSpeed = 200;
	public static final float maxLinearAcceleration = 1000;
	public static final float maxAngularSpeed = 10;
	public static final float maxAngularAcceleration = 5;
	
	public static final int boundingRadius = 500;
	public static final int decelerationRadius = 0;
	
	public static final String spriteId = "scissorfish_swim";
	
	public Scissorfish(PlayState state, World world, OrthographicCamera camera, RayHandler rays, int x, int y) {
		super(state, world, camera, rays, x, y, width, height, hbWidth, hbHeight, scale, spriteId,
				maxLinearSpeed, maxLinearAcceleration, maxAngularSpeed, maxAngularAcceleration, boundingRadius, decelerationRadius);
		
		//default enemy weapon is a slow ranged projectile
		this.weapon = new ScissorfishAttack(this);	
	}

}
