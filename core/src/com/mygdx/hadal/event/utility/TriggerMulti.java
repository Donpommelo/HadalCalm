package com.mygdx.hadal.event.utility;

import com.badlogic.gdx.utils.Array;
import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.states.PlayState;

/**
 * A MultiTrigger is an event that can trigger multiple events simultaneously.
 * <p>
 * Triggered Behavior: When triggered, this will trigger all events in its triggered list
 * Triggering Behavior: N/A. This event does nothing with its connectedEvent. Instead, it has a triggered list that is filled
 * when parsing the map.
 * <p>
 * Fields:
 * <p>
 * triggeringId: This string should be a comma-separated list of triggeredIds of events that can be triggered.
 * NO SPACES IN THIS LIST
 * @author Crempus Crarzendwich
 */
public class TriggerMulti extends Event {

	//this is a list of all of the events that this event triggers
	private final Array<Event> triggered = new Array<>();
	
	public TriggerMulti(PlayState state) {
		super(state);
	}
	
	@Override
	public void create() {
		this.eventData = new EventData(this) {
			
			@Override
			public void onActivate(EventData activator, Player p) {
				for (Event e : triggered) {
					if (e != null) {
						if (e.getEventData() != null) {
							e.getEventData().preActivate(this, p);
						}
					}
				}
			}
		};
	}
	
	public void addTrigger(Event e) { 
		if (e != null) {
			triggered.add(e); 
		}
	}
}
