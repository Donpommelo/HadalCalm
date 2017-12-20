package com.mygdx.hadal.schmucks.userdata;

import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.schmucks.UserDataTypes;
import com.mygdx.hadal.schmucks.bodies.HadalEntity;

public class HadalData {

	private int numContacts;
	private UserDataTypes type;
	
	private HadalEntity entity;
	
	public HadalData(World world, UserDataTypes type, HadalEntity entity) {
		this.type = type;
		this.entity = entity;
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
	
	public HadalEntity getEntity() {
		return entity;
	}
	
}
