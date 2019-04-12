package com.mygdx.hadal.event;

import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.states.PlayState;

/**
 * When touched, this event will set as a save point for the player its connected event. 
 * In certain circumstances, the player may be warped back to the last save point, such as respawning.
 * 
 * Triggered Behavior: Set the playstate's save point.
 * Triggering Behavior: This event will be the new save point
 * 
 * @author Zachary Tu
 *
 */
public class SaveSetter extends Event {

	private static final String name = "Save Point";

	public SaveSetter(PlayState state) {
		super(state, name);
	}
	
	@Override
	public void create() {
		this.eventData = new EventData(this) {
			
			@Override
			public void onActivate(EventData activator, Player p) {
				if (event.getConnectedEvent() != null) {
					state.setSafe((int)event.getConnectedEvent().getPosition().x, (int)event.getConnectedEvent().getPosition().y);
				}
			}
		};
	}
}
