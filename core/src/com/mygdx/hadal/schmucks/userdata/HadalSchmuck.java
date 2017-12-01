package com.mygdx.hadal.schmucks.userdata;

import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.schmucks.UserDataTypes;

public class HadalSchmuck {

	private int numContacts;
	private UserDataTypes type;
	
	public HadalSchmuck(World world, UserDataTypes type) {
		this.type = type;
		this.numContacts = 0;
	}

	public int getNumContacts() {
		return numContacts;
	}

	public void setNumContacts(int numContacts) {
		this.numContacts = numContacts;
	}

	public UserDataTypes getType() {
		return type;
	}
	
}
