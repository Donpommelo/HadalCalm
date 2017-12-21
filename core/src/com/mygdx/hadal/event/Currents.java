package com.mygdx.hadal.event;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.schmucks.bodies.HadalEntity;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.b2d.BodyBuilder;

import box2dLight.RayHandler;

public class Currents extends Event {
	
	private Vector2 vec;
	
	public Currents(PlayState state, World world, OrthographicCamera camera, RayHandler rays, int width, int height, int x, int y, Vector2 vec) {
		super(state, world, camera, rays, width, height, x, y);
		this.vec = vec;
		state.create(this);
	}
	
	public void create() {

		this.eventData = new EventData(world, this);
		
		this.body = BodyBuilder.createBox(world, startX, startY, width, height, 1, 1, 0, true, true, Constants.BIT_SENSOR, 
				(short) (Constants.BIT_PLAYER | Constants.BIT_ENEMY | Constants.BIT_PROJECTILE),
				(short) 0, true, eventData);
	}
	
	public void controller(float delta) {
		for (HadalEntity entity : eventData.schmucks) {
			entity.body.applyForceToCenter(vec, true);
		}
	}
	
	public void render(SpriteBatch batch) {
		
	}
	
}
