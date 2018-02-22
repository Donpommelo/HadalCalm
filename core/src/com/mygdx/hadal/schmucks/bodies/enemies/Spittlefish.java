package com.mygdx.hadal.schmucks.bodies.enemies;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.equip.enemy.SpittlefishAttack;
import com.mygdx.hadal.states.PlayState;

import box2dLight.RayHandler;

public class Spittlefish extends FloatingEnemy {

	private static final int width = 197;
	private static final int height = 76;
	
	private static final int hbWidth = 76;
	private static final int hbHeight = 197;
	
	private static final float scale = 0.25f;
	
	private static final float maxLinearSpeed = 75;
	private static final float maxLinearAcceleration = 800;
	private static final float maxAngularSpeed = 45;
	private static final float maxAngularAcceleration = 10;
	
	private static final int boundingRadius = 500;
	private static final int decelerationRadius = 100;
	
	private static final String spriteId = "spittlefish_swim";

	public Spittlefish(PlayState state, World world, OrthographicCamera camera, RayHandler rays, int x, int y) {
		super(state, world, camera, rays, x, y, width, height, hbWidth, hbHeight, scale, spriteId,
				maxLinearSpeed, maxLinearAcceleration, maxAngularSpeed, maxAngularAcceleration, boundingRadius, decelerationRadius);
		
		this.weapon = new SpittlefishAttack(this);	
	}

}
