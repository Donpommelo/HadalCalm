package com.mygdx.hadal.event.utility;

import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.states.PlayState;

/**
 * An objective changes an e oushfairslhgalriu tba
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
			public void onActivate(EventData activator) {
				if (event.getConnectedEvent() != null) {
					state.setObjectiveTarget(event.getConnectedEvent());
				}
			}
		};
	}
}
