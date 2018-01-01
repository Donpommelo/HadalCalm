package com.mygdx.hadal.schmucks.userdata;

import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.schmucks.UserDataTypes;
import com.mygdx.hadal.schmucks.bodies.HadalEntity;

public class FeetData extends HadalData {

	public Event terrain;
	
	public FeetData(World world, UserDataTypes type, HadalEntity entity) {
		super(world, type, entity);
	}

}
