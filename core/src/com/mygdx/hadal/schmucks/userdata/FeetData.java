package com.mygdx.hadal.schmucks.userdata;

import java.util.ArrayList;

import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.schmucks.UserDataTypes;
import com.mygdx.hadal.schmucks.bodies.Player;

/**
 * FeetData are attached to sensors used by the player to determine whether they are grounded or not.
 * Additionally, feet are used to process when the player is standing on top of certain events loike dropthrough platforms.
 * @author Zachary Tu
 *
 */
public class FeetData extends HadalData {

	//This is the event we are standing on, if existant
	private ArrayList<Event> terrain = new ArrayList<Event>();
	
	public FeetData(UserDataTypes type, Player player) {
		super(type, player);
	}

	public ArrayList<Event> getTerrain() { return terrain; }
}
