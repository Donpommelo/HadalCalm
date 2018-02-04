package com.mygdx.hadal.event.userdata;

import java.util.HashSet;
import java.util.Set;

import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.schmucks.UserDataTypes;
import com.mygdx.hadal.schmucks.bodies.HadalEntity;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.schmucks.userdata.HadalData;

public class EventData extends HadalData {

	protected Event event;
	
	public Set<HadalEntity> schmucks;

	public EventData(World world, Event event) {
		super(world, UserDataTypes.EVENT, event);
		this.event = event;
		this.schmucks = new HashSet<HadalEntity>();
	}
	
	public EventData(World world, Event event, UserDataTypes type) {
		super(world, type, event);
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
}
