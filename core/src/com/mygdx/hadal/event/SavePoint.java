package com.mygdx.hadal.event;

import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.states.PlayState;

/**
 * When touched, this event will be set as a save point for the player. In certain circumstances, the player may be warped back
 * to the last save point.
 * 
 * 
 * 
 * @author Zachary Tu
 *
 */
public class SavePoint extends Event {

	private static final String name = "Save Point";

	public SavePoint(PlayState state) {
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
