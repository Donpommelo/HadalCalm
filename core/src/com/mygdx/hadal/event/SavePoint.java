package com.mygdx.hadal.event;

import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.event.userdata.EventData;
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

	public SavePoint(PlayState state, int width, int height, int x, int y) {
		super(state, name, width, height, x, y);
	}
	
	@Override
	public void create() {
		this.eventData = new EventData(this) {
			
			@Override
			public void onActivate(EventData activator) {
				if (event.getConnectedEvent() != null) {
					state.setSafe((int)event.getConnectedEvent().getPosition().x, (int)event.getConnectedEvent().getPosition().y);
				}
			}
		};
	}
}
