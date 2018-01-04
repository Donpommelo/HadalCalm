package com.mygdx.hadal.schmucks.bodies.enemies;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.equip.enemy.SpittlefishAttack;
import com.mygdx.hadal.states.PlayState;

import box2dLight.RayHandler;

public class Spittlefish extends FloatingEnemy {

	public static final int width = 197;
	public static final int height = 76;
	
	public static final int hbWidth = 76;
	public static final int hbHeight = 197;
	
	public static final float scale = 0.25f;
	
	public static final float maxLinearSpeed = 75;
	public static final float maxLinearAcceleration = 800;
	public static final float maxAngularSpeed = 45;
	public static final float maxAngularAcceleration = 10;
	
	public static final int boundingRadius = 500;
	public static final int decelerationRadius = 100;
	
	public static final String spriteId = "spittlefish_swim";

	public Spittlefish(PlayState state, World world, OrthographicCamera camera, RayHandler rays, int x, int y) {
		super(state, world, camera, rays, x, y, width, height, hbWidth, hbHeight, scale, spriteId,
				maxLinearSpeed, maxLinearAcceleration, maxAngularSpeed, maxAngularAcceleration, boundingRadius, decelerationRadius);
		
		//default enemy weapon is a slow ranged projectile
		this.weapon = new SpittlefishAttack(this);	
	}

}
