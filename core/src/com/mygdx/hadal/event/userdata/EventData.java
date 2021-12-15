package com.mygdx.hadal.event.userdata;

import com.badlogic.gdx.utils.ObjectSet;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.schmucks.UserDataType;
import com.mygdx.hadal.schmucks.bodies.HadalEntity;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.server.packets.Packets;

/**
 * This is the data for an event. It contains the information needed for the event to activate and be activated
 * @author Scudding Scaguana
 */
public class EventData extends HadalData {

	//this is the event that owns this data
	protected final Event event;
	
	//This is a list of the schmucks touching the event
	protected final ObjectSet<HadalEntity> schmucks;

	public EventData(Event event) {
		super(UserDataType.EVENT, event);
		this.event = event;
		this.schmucks = new ObjectSet<>();
	}
	
	public EventData(Event event, UserDataType type) {
		super(type, event);
		this.event = event;
		this.schmucks = new ObjectSet<>();
	}
	
	/**
	 * This is activated when the event touches something
	 * @param fixB: The thing it touches
	 */
	public void onTouch(HadalData fixB) {
		if (fixB != null) {	
			schmucks.add(fixB.getEntity());
		}
	}
	
	/**
	 * This is activated when the event stops touching something
	 * @param fixB: The thing it stops touching
	 */
	public void onRelease(HadalData fixB) {
		if (fixB != null) {
			schmucks.remove(fixB.getEntity());
		}
	}

	/**
	 * This is called when something interacts with this event. Overridden in interactable event data for events like switches
	 * @param p: The player that interacts with this
	 */
	public void onInteract(Player p) {}
	
	/**
	 * This is called when something activates this event on the server side.
	 * @param activator: The event that activated this
	 * @param p: The player that activated this (or started this chain of event activation)
	 */
	public void preActivate(EventData activator, Player p) {

		//activation depends on event sync type
		switch(event.getSyncType()) {
		case USER:
			if (p == null) {
				onActivate(activator, null);
			} else {
				if (p.equals(event.getState().getPlayer())) {
					onActivate(activator, p);
				} else if (event.getState().isServer()) {
					HadalGame.server.sendToTCP(p.getConnId(), new Packets.ActivateEvent(event.getEntityID(), p.getConnId()));
				}
			}
			break;
		case ALL:
			onActivate(activator, p);
			if (p == null) {
				HadalGame.server.sendToAllTCP(new Packets.ActivateEvent(event.getEntityID(), -1));
			} else {
				HadalGame.server.sendToAllTCP(new Packets.ActivateEvent(event.getEntityID(), p.getConnId()));
			}
			break;
		case ILLUSION:
		case SERVER:
			onActivate(activator, p);
			break;
		}
	}
	
	/**
	 * This is what happens when an event is activated.
	 * @param activator: the event that activates this event
	 * @param p: the player that activates this event
	 */
	public void onActivate(EventData activator, Player p) {}
	
	public Event getEvent() { return event; }

	public ObjectSet<HadalEntity> getSchmucks() {	return schmucks; }
}
