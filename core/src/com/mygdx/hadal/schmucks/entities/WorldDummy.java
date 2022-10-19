package com.mygdx.hadal.schmucks.entities;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.constants.Constants;

/**
 * The World Dummy is a schmuck that effects are attributed to when there is no other schmuck to attribute them to.
 * For example, damage dealt by a hazard in the map is inflicted by the world dummy.
 * @author Nertaboo Nosinger
 */
public class WorldDummy extends Schmuck {

	public WorldDummy(PlayState state) {
		super(state, new Vector2(-1000, -1000), new Vector2(1, 1), "WORLD DUMMY",
				Constants.ENEMY_HITBOX, 99999.0f);
	}
	
	//we want this entity to not send any sync packets to the client because it never changes.
	@Override
	public void onServerSync() {}
}
