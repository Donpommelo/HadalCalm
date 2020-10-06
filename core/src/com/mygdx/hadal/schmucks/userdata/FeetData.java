package com.mygdx.hadal.schmucks.userdata;

import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.schmucks.UserDataTypes;
import com.mygdx.hadal.schmucks.bodies.HadalEntity;

import java.util.ArrayList;

/**
 * FeetData are attached to sensors used by the player to determine whether they are grounded or not.
 * Additionally, feet are used to process when the player is standing on top of certain events like dropthrough platforms.
 * @author Zachary Tu
 */
public class FeetData extends HadalData {

	//These are the event(s) we are standing on, if existent
	private final ArrayList<Event> terrain = new ArrayList<>();
	
	public FeetData(UserDataTypes type, HadalEntity entity) {
		super(type, entity);
	}

	public ArrayList<Event> getTerrain() { return terrain; }
}
