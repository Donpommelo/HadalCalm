package com.mygdx.hadal.schmucks.userdata;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.schmucks.UserDataTypes;
import com.mygdx.hadal.schmucks.bodies.Player;

public class FeetData extends HadalData {

	//This is a hardcoded silly way of implementing the one effect that activates uponstepping on an enemy.
	private static float stompDamage = 15.0f;
	
	//This is the event we are standing on, if existant
	private Event terrain;
	
	//This is the haver of the foot
	private Player footHaver;
	
	public FeetData(UserDataTypes type, Player player) {
		super(type, player);
		this.footHaver = player;
	}

	public Event getTerrain() { return terrain; }

	public void setTerrain(Event terrain) { this.terrain = terrain;	}
	
	/**
	 * This triggers upon stomping on an enemy. Damage them if a specific artifact is equipped.
	 * @param fixB
	 */
	public void onStomp(HadalData fixB) {
		if (footHaver.isStomping()) {
			if (fixB instanceof BodyData) {
				fixB.receiveDamage(stompDamage, new Vector2(), footHaver.getBodyData(), true);
			}
		}
	}
}
