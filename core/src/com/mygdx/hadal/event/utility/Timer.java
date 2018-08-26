package com.mygdx.hadal.event.utility;

import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.states.PlayState;

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
	
	//These keep track of how long until this triggers its connected event and how many times it can trigger again.
	private float timeCount = 0;
	
	//Is the timer running
	private boolean on;
	
	private static final String name = "Timer";

	public Timer(PlayState state, float interval) {
		super(state, name);
		this.interval = interval;
		this.on = true;
	}
	
	@Override
	public void create() {

		this.eventData = new EventData(this){
			
			@Override
			public void onActivate(EventData activator) {
				((Timer)event).on = !((Timer)event).on;
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
				if (getConnectedEvent() != null) {
					getConnectedEvent().getEventData().onActivate(eventData);
				}
			}
		}
	}
}
