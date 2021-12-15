package com.mygdx.hadal.schmucks.userdata;

import com.badlogic.gdx.utils.Array;
import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.schmucks.UserDataType;
import com.mygdx.hadal.schmucks.bodies.HadalEntity;

/**
 * FeetData are attached to sensors used by the player to determine whether they are grounded or not.
 * Additionally, feet are used to process when the player is standing on top of certain events like dropthrough platforms.
 * @author Twinko Tuldaldwin
 */
public class FeetData extends HadalData {

	//These are the event(s) we are standing on, if existent
	private final Array<Event> terrain = new Array<>();
	
	public FeetData(UserDataType type, HadalEntity entity) {
		super(type, entity);
	}

	public Array<Event> getTerrain() { return terrain; }
}
