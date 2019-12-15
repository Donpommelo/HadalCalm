package com.mygdx.hadal.event.utility;

import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.states.PlayState;

/**
 * A Redirecttrigger is an event that can trigger another event while saying that another event did it.
 * This is needed because some events (moving platforms, portals) care about which event triggered them for positional purposes
 * although a different event may have been the one to actually trigger them.
 * 
 * For example a switch that moves a moving platform should trigger a Redirect-Trigger that triggers the platform. This Redirect-Trigger's
 * blameId should be the triggeringId of a position dummy that the moving platform moves towards.
 * 
 * Triggered Behavior: When triggered, this will trigger its connected event while saying that its blamed event did it.
 * Triggering Behavior: This event will be triggered by this. It should care about the event that triggers it.
 * 
 * Fields:
 * blameId: This is the triggeringId of the event that will receive the blame for triggering this trigger's connected event.
 * 
 * @author Zachary Tu
 *
 */
public class TriggerRedirect extends Event {

	private static final String name = "RedirectTrigger";

	private Event blame ;
	
	public TriggerRedirect(PlayState state) {
		super(state, name);
	}
	
	@Override
	public void create() {
		this.eventData = new EventData(this) {
			
			@Override
			public void onActivate(EventData activator, Player p) {
				
				if (event.getConnectedEvent() != null && blame != null) {
					event.getConnectedEvent().getEventData().preActivate(blame.getEventData(), p);
				}
			}
		};
	}
	
	public void setBlame(Event e) {
		blame = e;
	}
}
