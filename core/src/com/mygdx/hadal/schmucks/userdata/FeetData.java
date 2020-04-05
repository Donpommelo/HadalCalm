package com.mygdx.hadal.schmucks.userdata;

import java.util.ArrayList;

import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.schmucks.UserDataTypes;
import com.mygdx.hadal.schmucks.bodies.HadalEntity;

/**
 * FeetData are attached to sensors used by the player to determine whether they are grounded or not.
 * Additionally, feet are used to process when the player is standing on top of certain events loike dropthrough platforms.
 * @author Zachary Tu
 *
 */
public class FeetData extends HadalData {

	//These are the event(s) we are standing on, if existant
	private ArrayList<Event> terrain = new ArrayList<Event>();
	
	public FeetData(UserDataTypes type, HadalEntity entity) {
		super(type, entity);
	}

	public ArrayList<Event> getTerrain() { return terrain; }
}
