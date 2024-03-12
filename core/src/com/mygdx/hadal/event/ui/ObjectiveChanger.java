package com.mygdx.hadal.event.ui;

import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.states.PlayState;

/**
 * An ObjectiveChanger creates an extra ui element that can track the location of an off-screen event and shows the
 * player the direction to it in the perimeter of the screen.
 * <p>
 * Triggered Behavior: When triggered, this event makes the objective ui track the connected event.
 * Triggering Behavior: N/A. However, it uses its connected event as a point to make the ui element track
 * <p>
 * Fields:
 * displayOffScreen: boolean of whether to display the ui marker in the corner of the screen when it is off screen. Default: false
 * displayOnScreen: boolean of whether to display the ui marker on the objective when it is on screen. Default: false
 * icon: String sprite of what icon to use as the objective marker
 * 
 * @author Muwort Mollinaire
 */
public class ObjectiveChanger extends Event {

	private final boolean displayOffScreen, displayOnScreen, displayClearCircle;
	
	//what objective marker icon do we use?
	private final Sprite icon;
	
	public ObjectiveChanger(PlayState state, boolean displayOffScreen, boolean displayOnScreen, boolean displayClearCircle, String icon) {
		super(state);
		this.displayOffScreen = displayOffScreen;
		this.displayOnScreen = displayOnScreen;
		this.displayClearCircle = displayClearCircle;
		this.icon = Sprite.valueOf(icon);
	}
	
	@Override
	public void create() {
		this.eventData = new EventData(this) {
			
			@Override
			public void onActivate(EventData activator, Player p) {
				if (event.getConnectedEvent() != null) {
					state.getUiObjective().addObjective(event.getConnectedEvent(), icon, displayOffScreen, displayOnScreen, displayClearCircle);
				}
			}
		};
	}
	
	@Override
	public void loadDefaultProperties() {
		setServerSyncType(eventSyncTypes.SELF);
		setClientSyncType(eventSyncTypes.SELF);
	}
}
