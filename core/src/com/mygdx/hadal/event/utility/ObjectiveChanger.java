package com.mygdx.hadal.event.utility;

import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.states.PlayState;

/**
 * An ObjectiveChanger creates an extra ui element that can track the location of an off-screen event and shows the
 * player the direction to it in the perimeter of the screen.
 * 
 * Triggered Behavior: When triggered, this event makes the objective ui track the connected event.
 * Triggering Behavior: N/A. However, it uses its connected event as a point to make the ui element track
 * 
 * Fields: N/A
 * 
 * @author Zachary Tu
 *
 */
public class ObjectiveChanger extends Event {

	private static final String name = "Objective Changer";
	
	public ObjectiveChanger(PlayState state) {
		super(state, name);
	}
	
	@Override
	public void create() {
		this.eventData = new EventData(this) {
			
			@Override
			public void onActivate(EventData activator, Player p) {
				if (event.getConnectedEvent() != null) {
					state.setObjectiveTarget(event.getConnectedEvent());
				}
			}
		};
	}
}
