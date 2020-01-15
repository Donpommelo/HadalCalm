package com.mygdx.hadal.strategies.hitbox;

import com.mygdx.hadal.schmucks.UserDataTypes;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.HitboxStrategy;

/**
 * This strategy makes a hbox die when it touches a wall
 * @author Zachary Tu
 *
 */
public class ContactWallDie extends HitboxStrategy{
	
	public ContactWallDie(PlayState state, Hitbox proj, BodyData user) {
		super(state, proj, user);
	}
	
	@Override
	public void onHit(HadalData fixB) {
		if (fixB == null) {
			hbox.die();
		} else if (fixB.getType().equals(UserDataTypes.WALL)){
			hbox.die();
		}
	}
}
