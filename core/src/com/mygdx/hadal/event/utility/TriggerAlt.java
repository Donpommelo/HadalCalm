package com.mygdx.hadal.event.utility;

import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.states.PlayState;

/**
 * An AltTrigger is a trigger used to activate events with multiple ways to activate.
 * 
 * For example, usually activating a spawn makes it spawn enemies. An AltTrigger activating it could change its wave size instead.
 * 
 * Triggered Behavior: When triggered, this will trigger its connected event.
 * Triggering Behavior: This event will be triggered by this. It should have some special functionality for specifically being
 * 	triggered by an AltTrigger.
 * Alt-Triggered Behavior: When alt-triggered, this trigger changes its message field to that of the alt-trigger.
 * 
 * 
 * Fields:
 * message: String that will be sent to the connected event when triggering. This string is then parsed into information that the
 * 	receiving event can use.
 * 
 * List of Alt-Trigger functionality:
 * 
 * Another Alt-Trigger: used to set the alt-trigger's message
 * Conditional Trigger: used to change which event the conditional trigger activates.
 * 
 * @author Zachary Tu
 *
 */
public class TriggerAlt extends Event {

	private static final String name = "AltTrigger";

	private String message;
	
	public TriggerAlt(PlayState state, String message) {
		super(state, name);
		this.message = message;
	}
	
	@Override
	public void create() {
		this.eventData = new EventData(this) {
			
			@Override
			public void onActivate(EventData activator) {
				
				if (activator.getEvent() instanceof TriggerAlt) {
					setMessage(((TriggerAlt)activator.getEvent()).getMessage());
				} else {
					if (event.getConnectedEvent() != null) {
						event.getConnectedEvent().getEventData().onActivate(this);
					}
				}
			}
		};
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
