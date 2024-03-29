package com.mygdx.hadal.event.utility;

import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.states.PlayState;

/**
 * An EventDeleter. This Event will delete a specified event. very straightforwards.
 * <p>
 * Triggered Behavior: When triggered, this event will perform the deletion.
 * Triggering Behavior: The event that triggers this is deleted, so the triggering behavior just chains to another event.
 * <p>
 * Fields: N/A
 *
 * @author Bligpoulos Broronica
 */
public class EventDeleter extends Event {
	
	public EventDeleter(PlayState state) {
		super(state);
	}
	
	@Override
	public void create() {

		this.eventData = new EventData(this) {
			
			@Override
			public void onActivate(EventData activator, Player p) {
				activator.getEvent().queueDeletion();
				if (event.getConnectedEvent() != null) {
					event.getConnectedEvent().getEventData().preActivate(this, p);
				}
			}
		};
	}
}
