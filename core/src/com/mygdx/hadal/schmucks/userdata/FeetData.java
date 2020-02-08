package com.mygdx.hadal.schmucks.userdata;

import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.schmucks.UserDataTypes;
import com.mygdx.hadal.schmucks.bodies.Player;

public class FeetData extends HadalData {

	//This is the event we are standing on, if existant
	private Event terrain;
	
	
	public FeetData(UserDataTypes type, Player player) {
		super(type, player);
	}

	public Event getTerrain() { return terrain; }

	public void setTerrain(Event terrain) { this.terrain = terrain;	}
}
