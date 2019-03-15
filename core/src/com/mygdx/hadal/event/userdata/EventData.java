package com.mygdx.hadal.event.userdata;

import java.util.HashSet;
import java.util.Set;

import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.schmucks.UserDataTypes;
import com.mygdx.hadal.schmucks.bodies.HadalEntity;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.server.Packets;

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
	
	public void preActivate(EventData activator, Player p) {
		
		if (p == null) {
			onActivate(activator, p);
			return;
		}
		
		switch(event.getSyncType()) {
		case 0:
			onActivate(activator, p);
			break;
		case 1:
			if (p.equals(event.getState().getPlayer())) {
				onActivate(activator, p);
			} else {
				HadalGame.server.sendPacketToPlayer(p, new Packets.ActivateEvent(event.getEntityID().toString()));
			}
			break;
		case 2:
			onActivate(activator, p);
			HadalGame.server.sendPacketToPlayer(p, new Packets.ActivateEvent(event.getEntityID().toString()));
			break;
		case 3:
			onActivate(activator, p);
			break;
		}
	}
	
	public void onActivate(EventData activator, Player p) {
		
	}
	
	public Event getEvent() {
		return event;
	}

	public Set<HadalEntity> getSchmucks() {
		return schmucks;
	}
	
}
