package com.mygdx.hadal.event.ui;

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
 * Fields:
 * 
 * display: boolean of whether to display the ui marker in the corner of the screen. Default: false
 * 
 * @author Zachary Tu
 *
 */
public class ObjectiveChanger extends Event {

	//do we display the objective marker?
	private boolean display;
	
	public ObjectiveChanger(PlayState state, boolean display) {
		super(state);
		this.display = display;
	}
	
	@Override
	public void create() {
		this.eventData = new EventData(this) {
			
			@Override
			public void onActivate(EventData activator, Player p) {
				if (event.getConnectedEvent() != null) {
					state.setObjectiveTarget(event.getConnectedEvent());
				}
				state.setDisplayObjective(display);
			}
		};
	}
	
	@Override
	public void loadDefaultProperties() {
		setSyncType(eventSyncTypes.USER);
	}
}
