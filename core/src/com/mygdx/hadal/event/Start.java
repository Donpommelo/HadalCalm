package com.mygdx.hadal.event;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.states.PlayState;

/**
 * 
 * @author Zachary Tu
 *
 */
public class Start extends Event {

	private boolean used, alreadyUsed;
	
	public Start(PlayState state, Vector2 startPos, Vector2 size, boolean used) {
		super(state, startPos, size);
		this.used = used;
		alreadyUsed = false;
	}
	
	@Override
	public void create() {
		this.eventData = new EventData(this);
	}
	
	@Override
	public void controller(float delta) {
		if (!alreadyUsed) {
			alreadyUsed = true;
			if (used) {
				if (this.getConnectedEvent() != null) {
					this.getConnectedEvent().getEventData().preActivate(eventData, null);
				}
			}
		}
	}
}
