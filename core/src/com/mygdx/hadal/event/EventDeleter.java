package com.mygdx.hadal.event;

import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.states.PlayState;

/**
 * An EventDeleter. This Event will delete a specified event. very straightforwards.
 * 
 * Triggered Behavior: When triggered, this eventwill perform the deletion.
 * Triggering Behavior: The connected event is the one who will be deleted.
 *  
 * Fields: N/A
 * 
 * @author Zachary Tu
 *
 */
public class EventDeleter extends Event {
	
	private static final String name = "Event Deleter";

	public EventDeleter(PlayState state) {
		super(state, name);
	}
	
	@Override
	public void create() {

		this.eventData = new EventData(this) {
			
			@Override
			public void onActivate(EventData activator) {
				
				activator.getEvent().queueDeletion();
				
				if (event.getConnectedEvent() != null) {
					event.getConnectedEvent().getEventData().onActivate(this);
				}
			}
		};
	}
	
}
