package com.mygdx.hadal.schmucks.strategies;

import com.mygdx.hadal.schmucks.UserDataTypes;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.states.PlayState;

/**
 * This strategy makes an attached hbox lose durability upon contact with a wall.
 * If a hbox's durability reaches 0, it dies.
 * @author Zachary Tu
 *
 */
public class HitboxOnContactWallLoseDuraStrategy extends HitboxStrategy{
	
	public HitboxOnContactWallLoseDuraStrategy(PlayState state, Hitbox proj, BodyData user) {
		super(state, proj, user);
	}
	
	@Override
	public void onHit(HadalData fixB) {
		if (fixB == null) {
			hbox.lowerDurability();
		} else if (fixB.getType().equals(UserDataTypes.WALL)){
			hbox.lowerDurability();
		}
	}
}
