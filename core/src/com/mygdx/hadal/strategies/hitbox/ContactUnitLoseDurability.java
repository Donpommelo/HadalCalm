package com.mygdx.hadal.strategies.hitbox;

import com.mygdx.hadal.schmucks.UserDataTypes;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.HitboxStrategy;

/**
 * This strategy makes an attached hbox lose durability upon contact with a unit.
 * If a hbox's durability reaches 0, it dies.
 * @author Zachary Tu
 *
 */
public class ContactUnitLoseDurability extends HitboxStrategy{
	
	public ContactUnitLoseDurability(PlayState state, Hitbox proj, BodyData user) {
		super(state, proj, user);
	}
	
	@Override
	public void onHit(HadalData fixB) {
		if (fixB != null) {
			if (fixB.getType().equals(UserDataTypes.BODY)) {
				hbox.lowerDurability();
			}
		}
	}
}
