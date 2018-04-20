package com.mygdx.hadal.event.userdata;

import java.util.HashSet;
import java.util.Set;

import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.schmucks.UserDataTypes;
import com.mygdx.hadal.schmucks.bodies.HadalEntity;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.schmucks.userdata.HadalData;

public class EventData extends HadalData {

	protected Event event;
	
	protected Set<HadalEntity> schmucks;

	public EventData(Event event) {
		super(UserDataTypes.EVENT, event);
		this.event = event;
		this.schmucks = new HashSet<HadalEntity>();
	}
	
	public EventData(Event event, UserDataTypes type) {
		super(type, event);
		this.event = event;
		this.schmucks = new HashSet<HadalEntity>();
	}
	
	public void onTouch(HadalData fixB) {
		if (fixB != null) {	
			schmucks.add(fixB.getEntity());
		}
	}
	
	public void onRelease(HadalData fixB) {
		if (fixB != null) {
			schmucks.remove(fixB.getEntity());
		}
	}

	public void onInteract(Player p) {
		
	}
	
	public void onActivate(EventData activator) {
		
	}
	
	public Event getEvent() {
		return event;
	}

	public Set<HadalEntity> getSchmucks() {
		return schmucks;
	}
	
}
