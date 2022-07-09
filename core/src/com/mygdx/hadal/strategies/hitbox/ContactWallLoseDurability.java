package com.mygdx.hadal.strategies.hitbox;

import com.mygdx.hadal.schmucks.UserDataType;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.HitboxStrategy;

/**
 * This strategy makes an attached hbox lose durability upon contact with a wall.
 * If a hbox's durability reaches 0, it dies.
 * @author Riximus Ringcott
 */
public class ContactWallLoseDurability extends HitboxStrategy {
	
	public ContactWallLoseDurability(PlayState state, Hitbox proj, BodyData user) {
		super(state, proj, user);
	}
	
	@Override
	public void onHit(HadalData fixB) {
		if (fixB != null) {
			if (UserDataType.WALL.equals(fixB.getType())) {
				hbox.lowerDurability();
			}
		}
	}
}
