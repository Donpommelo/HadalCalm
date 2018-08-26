package com.mygdx.hadal.event;

import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.states.PlayState;

/**
 * A radio is a simple event that when activated will put up a test dialogue actor into the play stage
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

	private static final String name = "Radio";

	private String id;
	
	public Dialog(PlayState state, String id) {
		super(state, name);
		this.id = id;
	}
	
	@Override
	public void create() {

		this.eventData = new EventData(this) {
			
			@Override
			public void onActivate(EventData activator) {
				if (event.getConnectedEvent() != null) {
					state.getStage().addDialogue(id, this, event.getConnectedEvent().getEventData());
				} else {
					state.getStage().addDialogue(id, this, null);
				}
				
			}
		};
	}
}
