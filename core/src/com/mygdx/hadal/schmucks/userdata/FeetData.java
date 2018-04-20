package com.mygdx.hadal.schmucks.userdata;

import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.schmucks.UserDataTypes;
import com.mygdx.hadal.schmucks.bodies.HadalEntity;

public class FeetData extends HadalData {

	private Event terrain;
	
	public FeetData(UserDataTypes type, HadalEntity entity) {
		super(type, entity);
	}

	public Event getTerrain() {
		return terrain;
	}

	public void setTerrain(Event terrain) {
		this.terrain = terrain;
	}
	
}
