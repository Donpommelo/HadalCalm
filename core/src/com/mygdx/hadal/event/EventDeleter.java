package com.mygdx.hadal.event;

import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.states.PlayState;

/**
 * An event deleter TBA
 * @author Zachary Tu
 *
 */
public class EventDeleter extends Event {
	
	private static final String name = "Event Deleter";

	public EventDeleter(PlayState state, int width, int height,
			int x, int y) {
		super(state, name, width, height, x, y);
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
