package com.mygdx.hadal.event.ui;

import com.mygdx.hadal.effects.Sprite;
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
 * displayOffScreen: boolean of whether to display the ui marker in the corner of the screen when it is off screen. Default: false
 * displayOnScreen: boolean of whether to display the ui marker on the objective when it is on screen. Default: false
 * icon: String sprite of what icon to use as the objective marker
 * 
 * @author Zachary Tu
 */
public class ObjectiveChanger extends Event {

	//do we display the objective marker?
	private final boolean displayOffScreen, displayOnScreen;
	
	//what objective marker icon do we use?
	private final Sprite icon;
	
	public ObjectiveChanger(PlayState state, boolean displayOffScreen, boolean displayOnScreen, String icon) {
		super(state);
		this.displayOffScreen = displayOffScreen;
		this.displayOnScreen = displayOnScreen;
		this.icon = Sprite.valueOf(icon);
	}
	
	@Override
	public void create() {
		this.eventData = new EventData(this) {
			
			@Override
			public void onActivate(EventData activator, Player p) {
				if (event.getConnectedEvent() != null) {
					state.getUiObjective().setObjectiveTarget(event.getConnectedEvent());
					state.getUiObjective().setDisplayObjectiveOffScreen(displayOffScreen);
					state.getUiObjective().setDisplayObjectiveOnScreen(displayOnScreen);
					state.getUiObjective().setIconType(icon);
				}
			}
		};
	}
	
	@Override
	public void loadDefaultProperties() {
		setSyncType(eventSyncTypes.USER);
	}
}
