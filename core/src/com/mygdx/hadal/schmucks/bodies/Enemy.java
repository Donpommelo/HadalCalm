package com.mygdx.hadal.schmucks.bodies;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.b2d.BodyBuilder;

import box2dLight.RayHandler;

public class Enemy extends Schmuck{
		
	public Enemy(PlayState state, World world, OrthographicCamera camera, RayHandler rays, float width, float height) {
		super(state, world, camera, rays);
		
		this.body = BodyBuilder.createBox(world, 500, 300, width, height, false, true, Constants.BIT_ENEMY, 
				(short) (Constants.BIT_WALL | Constants.BIT_SENSOR | Constants.BIT_PROJECTILE | Constants.BIT_PLAYER),
				Constants.ENEMY_HITBOX);
		
		this.bodyData = new BodyData(world, this);
		body.getFixtureList().get(0).setUserData(bodyData);
	}
	
	public void create() {
		
	}

	public void controller(float delta) {

	}
	
	public void render(SpriteBatch batch) {
		
	}
	
	public void dispose() {
		super.dispose();
	}
}
