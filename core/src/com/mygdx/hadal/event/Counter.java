package com.mygdx.hadal.event;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.b2d.BodyBuilder;

import box2dLight.RayHandler;

/**
 * A Counter is an event that is both triggered by another event as well as triggers another event.
 * After it is triggered a certain number of times, it will trigger its connected event.
 * @author Zachary Tu
 *
 */
public class Counter extends Event {

	private static final String name = "Counter";

	int maxCount;
	int currentCount = 0;
	
	public Counter(PlayState state, World world, OrthographicCamera camera, RayHandler rays, int width, int height,
			int x, int y, int maxCount) {
		super(state, world, camera, rays, name, width, height, x, y);
		this.maxCount = maxCount;
	}
	
	@Override
	public void create() {
		this.eventData = new EventData(world, this) {
			
			@Override
			public void onActivate(EventData activator) {
				currentCount++;
				if (currentCount >= maxCount && event.getConnectedEvent() != null) {
					event.getConnectedEvent().eventData.onActivate(this);
					currentCount = 0;
				}
			}
		};
		
		this.body = BodyBuilder.createBox(world, startX, startY, width, height, 1, 1, 0, true, true, Constants.BIT_SENSOR, 
				(short) (Constants.BIT_PLAYER),
				(short) 0, true, eventData);
	}
}
