package com.mygdx.hadal.event.utility;

import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.states.PlayState;

/**
 * An AltTrigger is a trigger used to activate events with multiple ways to activate.
 * <p>
 * For example, usually activating a spawn makes it spawn enemies. An AltTrigger activating it could change its wave size instead.
 * <p>
 * Triggered Behavior: When triggered, this will trigger its connected event.
 * Triggering Behavior: This event will be triggered by this. It should have some special functionality for specifically being
 * 	triggered by an AltTrigger.
 * Alt-Triggered Behavior: When alt-triggered, this trigger changes its message field to that of the alt-trigger.
 * <p>
 * 
 * Fields:
 * message: String that will be sent to the connected event when triggering. This string is then parsed into information that the
 * 	receiving event can use.
 * <p>
 * List of Alt-Trigger functionality:
 * <p>
 * Another Alt-Trigger: used to set the alt-trigger's message
 * Conditional Trigger: used to change which event the conditional trigger activates.
 * Equipment Pickup: used to set the equip to a specific weapon or reroll it
 * Schmuck Spawner: used to change the number of enemies the spawner will spawn
 * Timer: used to start, stop or reset the timer
 *
 * @author Lepepper Lolympia
 */
public class TriggerAlt extends Event {

	private String message;
	
	public TriggerAlt(PlayState state, String message) {
		super(state);
		this.message = message;
	}
	
	@Override
	public void create() {
		this.eventData = new EventData(this) {
			
			@Override
			public void onActivate(EventData activator, Player p) {
				
				if (null != activator && activator.getEvent() instanceof TriggerAlt trigger) {
					setMessage(trigger.getMessage());
				} else {
					if (event.getConnectedEvent() != null) {
						event.getConnectedEvent().getEventData().preActivate(this, p);
					}
				}
			}
		};
	}

	public String getMessage() { return message; }

	public void setMessage(String message) { this.message = message; }
}
