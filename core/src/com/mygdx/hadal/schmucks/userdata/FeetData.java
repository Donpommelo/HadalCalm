package com.mygdx.hadal.schmucks.userdata;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.schmucks.UserDataTypes;
import com.mygdx.hadal.schmucks.bodies.Schmuck;

public class FeetData extends HadalData {

	private static float stompDamage = 15.0f;
	private Event terrain;
	private Schmuck footHaver;
	
	public FeetData(UserDataTypes type, Schmuck entity) {
		super(type, entity);
		this.footHaver = entity;
	}

	public Event getTerrain() {
		return terrain;
	}

	public void setTerrain(Event terrain) {
		this.terrain = terrain;
	}
	
	public void onStomp(HadalData fixB) {
		if (footHaver.isStomping()) {
			if (fixB instanceof BodyData) {
				fixB.receiveDamage(stompDamage, new Vector2(0, 0), footHaver.getBodyData(), null, true);
			}
		}
	}
}
