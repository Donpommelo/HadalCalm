package com.mygdx.hadal.strategies.hitbox;

import com.mygdx.hadal.schmucks.UserDataTypes;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.HitboxStrategy;

/**
 * This strategy makes the attached hbox deflect other hboxes it hits.
 * Deflecting just changes the momentum of the other hbox.
 * @author Zachary Tu
 *
 */
public class ContactDestroyProjectiles extends HitboxStrategy {
	
	public ContactDestroyProjectiles(PlayState state, Hitbox proj, BodyData user) {
		super(state, proj, user);
	}
	
	@Override
	public void onHit(HadalData fixB) {
		if (fixB != null) {
			if (fixB.getType().equals(UserDataTypes.HITBOX)){
				fixB.getEntity().queueDeletion();
			}
		}
	}
}
