package com.mygdx.hadal.event.ui;

import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.states.PlayState;

/**
 * A UIChanger changes the UI. specifically, the UILevel (name tentative) actor to display different information or change 
 * some extra, non-score field like lives.
 * 
 * Triggered Behavior: When triggered, this event will change the text that appears in the upper right-hand side of the screen.
 * Triggering Behavior: N/A
 * 
 * Fields:
 * types: This string specifies the uiType enums that will be used for the ui change. This is a comma-separated list of enum names. (or just the text for misc labels)
 * changeType: this is an boolean that specifies whether we clear existing tags before adding these. Default: true
 * 
 * @author Zachary Tu
 */
public class UIChanger extends Event {

	private final String types;
	private final boolean changeType;
	
	public UIChanger(PlayState state, String types, boolean changeType) {
		super(state);
		this.types = types;
		this.changeType = changeType;
	}
	
	@Override
	public void create() {
		this.eventData = new EventData(this) {
			
			@Override
			public void onActivate(EventData activator, Player p) {
				state.getUiExtra().changeTypes(types, changeType);
			}
		};
	}
	
	@Override
	public void loadDefaultProperties() {
		setSyncType(eventSyncTypes.ALL);
	}
}
