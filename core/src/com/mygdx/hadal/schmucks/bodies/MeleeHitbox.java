package com.mygdx.hadal.schmucks.bodies;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.states.PlayState;

import box2dLight.RayHandler;

public class MeleeHitbox extends Hitbox {

	public Vector2 center;
	
	public MeleeHitbox(PlayState state, float x, float y, int width, int height, float lifespan,
			Vector2 startAngle, Vector2 center, short filter, World world, OrthographicCamera camera, RayHandler rays, HadalEntity user) {
		super(state, x, y, width, height, 0, lifespan, 0, 0, startAngle, filter, true, world, camera, rays, user);
		this.center = center;
	}
	
	public void controller(float delta) {
		Vector2 hbLocation = creator.getPosition().add(center);
		this.body.setTransform(hbLocation, startVelo.angleRad());
		super.controller(delta);
	}

}
