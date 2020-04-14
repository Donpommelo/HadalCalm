package com.mygdx.hadal.schmucks.bodies;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.Constants;

public class WorldDummy extends Schmuck {

	public WorldDummy(PlayState state) {
		super(state, new Vector2(-1000, -1000), new Vector2(1, 1), "WORLD DUMMY", Constants.ENEMY_HITBOX, 99999.0f);
	}
	
	@Override
	public void onServerSync() {}
}
