package com.mygdx.hadal.schmucks.userdata;

import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.schmucks.UserDataTypes;

public class FeetData extends HadalData{

	public FeetData(World world) {
		super(world, UserDataTypes.BODY);
	}

}
