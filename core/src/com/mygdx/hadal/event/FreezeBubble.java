package com.mygdx.hadal.event;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.schmucks.bodies.HadalEntity;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.b2d.BodyBuilder;

import box2dLight.RayHandler;

/**
 * Currents are an event that apply a continuous force to all schmucks inside of it.
 * @author Zachary Tu
 *
 */
public class FreezeBubble extends Event {
	

	//This keeps track of engine timer.
	private float controllerCount = 0;
	
	private short filter;
	
	private static final String name = "Freeze Bubble";

	public FreezeBubble(PlayState state, World world, OrthographicCamera camera, RayHandler rays, int width, int height,
			int x, int y, float duration, short filter) {
		super(state, world, camera, rays, name, width, height, x, y, duration);
		this.filter = filter;
	}
	
	@Override
	public void create() {

		this.eventData = new EventData(world, this);
		
		this.body = BodyBuilder.createBox(world, startX, startY, width, height, 1, 1, 0, true, true, Constants.BIT_SENSOR, 
				(short) (Constants.BIT_PLAYER | Constants.BIT_ENEMY | Constants.BIT_PROJECTILE),
				filter, true, eventData);
	}
	
	@Override
	public void controller(float delta) {
		controllerCount+=delta;
		if (controllerCount >= 1/60f) {
			controllerCount = 0;
			
			for (HadalEntity entity : eventData.getSchmucks()) {
				entity.getBody().setLinearVelocity(0, 0);
			}
		}
		super.controller(delta);
		
	}
	
}