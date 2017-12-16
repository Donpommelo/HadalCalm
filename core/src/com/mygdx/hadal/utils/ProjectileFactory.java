package com.mygdx.hadal.utils;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.states.PlayState;

import box2dLight.RayHandler;

public abstract class ProjectileFactory {
	
	public abstract void makeProjectile(PlayState state, Vector2 startVelocity, float x, float y, short filter,
			World world, OrthographicCamera camera, RayHandler rays);
}
