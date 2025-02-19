package com.mygdx.hadal.event.ui;

import com.badlogic.gdx.math.MathUtils;
import com.mygdx.hadal.actors.DialogBox.DialogType;
import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.states.PlayState;

/**
 * A Dialog is a simple event that when activated will put up a test dialogue actor into the play stage
 * <p>
 * Triggered Behavior: When triggered, this event puts up a test dialogue actor into the play stage
 * Triggering Behavior: This event will trigger its connected event when its dialog is finished
 * <p>
 * Fields:
 * id: string id of the conversation to be displayed. This can be a comma-separated list of dialog ids, in which a random one will be chosen.
 * 
 * @author Bromato Brardamom
 */
public class Dialog extends Event {

	private final String[] id;
	private final String type;
	
	public Dialog(PlayState state, String id, String type) {
		super(state);
		this.id = id.split(",");
		this.type = type;
	}
	
	@Override
	public void create() {

		this.eventData = new EventData(this) {
			
			@Override
			public void onActivate(EventData activator, Player p) {
				if (id.length != 0) {
					String dialogId = id[MathUtils.random(id.length - 1)];
					
					if (event.getConnectedEvent() != null) {
						state.getUIManager().getDialogBox().addDialogue(dialogId, this, event.getConnectedEvent().getEventData(), DialogType.valueOf(type));
					} else {
						state.getUIManager().getDialogBox().addDialogue(dialogId, this, null, DialogType.valueOf(type));
					}
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
