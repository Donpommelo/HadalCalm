package com.mygdx.hadal.event.utility;

import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.states.PlayState;

/**
 * A Counter is an event keeps track of the number of times it is triggered and can link to another event after specified numbers of triggerings.
 * 
 * Triggered Behavior: When triggered, this event increments its currentCount field.
 * Triggering Behavior: This event will trigger its connected event when its currentCount field reaches its maxCount field.
 * 
 * Fields:
 * count: maxCount. When this event is triggered this many times, it will trigger its connected event.
 * countStart: the number that currentCount will start at. Optional. Default: 0
 * 
 * @author Zachary Tu
 */
public class Counter extends Event {

	private int maxCount;
	private int currentCount;
	
	public Counter(PlayState state, int maxCount, int startCount) {
		super(state);
		this.maxCount = maxCount;
		this.currentCount = startCount;
	}
	
	@Override
	public void create() {
		this.eventData = new EventData(this) {
			
			@Override
			public void onActivate(EventData activator, Player p) {
				currentCount++;
				if (currentCount >= maxCount && event.getConnectedEvent() != null) {
					event.getConnectedEvent().getEventData().preActivate(this, p);
					currentCount = 0;
				}
			}
		};
	}
}
