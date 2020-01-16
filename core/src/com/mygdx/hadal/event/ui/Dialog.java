package com.mygdx.hadal.event.ui;

import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.states.PlayState;

/**
 * A Dialog is a simple event that when activated will put up a test dialogue actor into the play stage
 * 
 * Triggered Behavior: When triggered, this event puts up a test dialogue actor into the play stage
 * Triggering Behavior: This event will trigger its connected event when its dialog is finished
 * 
 * Fields:
 * id: string id of the conversation to be displayed.
 * 
 * @author Zachary Tu
 *
 */
public class Dialog extends Event {

	private String id;
	
	public Dialog(PlayState state, String id) {
		super(state);
		this.id = id;
	}
	
	@Override
	public void create() {

		this.eventData = new EventData(this) {
			
			@Override
			public void onActivate(EventData activator, Player p) {
				
				if (event.getConnectedEvent() != null) {
					state.getDialogBox().addDialogue(id, this, event.getConnectedEvent().getEventData());
				} else {
					state.getDialogBox().addDialogue(id, this, null);
				}
			}
		};
	}
	
	@Override
	public void loadDefaultProperties() {
		setSyncType(eventSyncTypes.USER);
	}
}