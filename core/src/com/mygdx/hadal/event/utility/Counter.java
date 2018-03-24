package com.mygdx.hadal.event.utility;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.states.PlayState;

import box2dLight.RayHandler;

/**
 * A Counter is an event that is both triggered by another event as well as triggers another event.
 * After it is triggered a certain number of times, it will trigger its connected event.
 * @author Zachary Tu
 *
 */
public class Counter extends Event {

	private static final String name = "Counter";

	private int maxCount;
	private int currentCount;
	
	private boolean oneTime, active;
	
	public Counter(PlayState state, World world, OrthographicCamera camera, RayHandler rays, int width, int height,
			int x, int y, int maxCount, int startCount, boolean oneTime) {
		super(state, world, camera, rays, name, width, height, x, y);
		this.maxCount = maxCount;
		this.currentCount = startCount;
		
		this.oneTime = oneTime;
		this.active = true;
	}
	
	@Override
	public void create() {
		this.eventData = new EventData(world, this) {
			
			@Override
			public void onActivate(EventData activator) {
				if (active) {
					currentCount++;
					if (currentCount >= maxCount && event.getConnectedEvent() != null) {
						event.getConnectedEvent().getEventData().onActivate(this);
						currentCount = 0;
						if (oneTime) {
							active = false;
						}
					}
				}
			}
		};
	}
}
