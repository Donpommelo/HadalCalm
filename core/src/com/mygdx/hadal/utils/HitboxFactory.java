package com.mygdx.hadal.utils;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.schmucks.bodies.HadalEntity;
import com.mygdx.hadal.schmucks.bodies.Hitbox;
import com.mygdx.hadal.states.PlayState;

import box2dLight.RayHandler;

public abstract class HitboxFactory {
	
	public abstract Hitbox makeHitbox(HadalEntity user, PlayState state, Vector2 startVelocity, float x, float y, short filter,
			World world, OrthographicCamera camera, RayHandler rays);
}
