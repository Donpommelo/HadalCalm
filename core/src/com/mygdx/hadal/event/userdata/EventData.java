package com.mygdx.hadal.event.userdata;

import com.badlogic.gdx.utils.ObjectSet;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.constants.UserDataType;
import com.mygdx.hadal.server.util.PacketManager;
import com.mygdx.hadal.schmucks.entities.HadalEntity;
import com.mygdx.hadal.schmucks.entities.Player;
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

		//activation depends on event sync type and whether this is the server or client
		switch (event.getState().isServer() ? event.getServerSyncType() : event.getClientSyncType()) {
		case IGNORE:
			return;
		case SELF:
			if (p != null) {
				if (p.equals(HadalGame.usm.getOwnPlayer())) {
					onActivate(activator, p);
				}
			}
			break;
		case USER:
			if (p == null) {
				onActivate(activator, null);
			} else {
				if (p.equals(HadalGame.usm.getOwnPlayer())) {
					onActivate(activator, p);
				} else if (event.getState().isServer()) {
					PacketManager.serverTCP(p.getUser().getConnID(), getActivationPacket(p));
				}
			}
			break;
		case ECHO_ACTIVATE:
			echoActivation(p);
			onActivate(activator, p);
			break;
		case ECHO:
			echoActivation(p);
			break;
		case ACTIVATE:
			onActivate(activator, p);
			break;
		case ECHO_ACTIVATE_EXCLUDE:
			if (null != p) {
				if (!event.getState().isServer()) {
					echoActivation(p);
				} else {
					PacketManager.serverTCPAllExcept(p.getUser().getConnID(), getActivationPacket(p));
				}
			}
			onActivate(activator, p);
			break;
		}
	}

	/**
	 * Helper function that echoes an event activation for server or client
	 */
	private void echoActivation(Player p) {
		if (event.getState().isServer()) {
			PacketManager.serverTCPAll(getActivationPacket(p));
		} else {
			PacketManager.clientTCP(getActivationPacket(p));
		}
	}

	/**
	 * Another helper function. When we echo an event activation, the packet depends on whether we can use the event's
	 * triggeredID or whether we have to use the UUID
	 */
	private Object getActivationPacket(Player p) {
		int connID = p == null ? -1 : p.getUser().getConnID();
		if (null == event.getTriggeredID()) {
			return new Packets.ActivateEvent(event.getEntityID(), connID);
		} else {
			return new Packets.ActivateEventByTrigger(event.getTriggeredID(), connID);
		}
	}

	/**
	 * This is what happens when an event is activated.
	 * @param activator: the event that activates this event
	 * @param p: the player that activates this event
	 */
	public void onActivate(EventData activator, Player p) {}
	
	public Event getEvent() { return event; }

	public ObjectSet<HadalEntity> getSchmucks() { return schmucks; }
}
