package com.mygdx.hadal.event.utility;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.states.PlayState;

import box2dLight.RayHandler;

/**
 * The Timer keeps track of time.
 * 
 * Triggered Behavior: When triggered, the timer will stop or start. This resets the timer
 * Triggering Behavior: When the timer reaches a specified value, this event will trigger its connected event
 * 
 * Fields:
 * interval: The time in seconds before this activates its connected event.
 * limit: The number of times this timer can trigger its connected event. If 0, then infinite. Optional. Default: 0
 * startOn: Does this timer start on or off? Optional. Default: true
 * @author Zachary Tu
 *
 */
public class Timer extends Event {
	
	//How frequently will this event trigger its connected event?
	private float interval;
	
	//The number of times this event can trigger its connected event.
	private int limit;
	
	//These keep track of how long until this triggers its connected event and how many times it can trigger again.
	private float timeCount = 0;
	private int amountCount = 0;
	
	//Is the timer running
	private boolean on;
	
	private static final String name = "Timer";

	public Timer(PlayState state, World world, OrthographicCamera camera, RayHandler rays, int width, int height,
			int x, int y, float interval, int limit, boolean startOn) {
		super(state, world, camera, rays, name, width, height, x, y);
		this.interval = interval;
		this.limit = limit;
		this.on = startOn;
	}
	
	@Override
	public void create() {

		this.eventData = new EventData(world, this){
			
			@Override
			public void onActivate(EventData activator) {
				((Timer)event).on = !((Timer)event).on;
				amountCount = 0;
				timeCount = 0;
			}
		};
	}
	
	@Override
	public void controller(float delta) {
		if (on) {
			timeCount += delta;
			if (timeCount >= interval) {
				timeCount = 0;
				amountCount++;
				if (getConnectedEvent() != null) {
					getConnectedEvent().getEventData().onActivate(eventData);
				}
			}
			if ((limit != 0 && amountCount >= limit)) {
				on = false;
			}
		}
	}
}
