package com.mygdx.hadal.strategies.hitbox;

import com.mygdx.hadal.schmucks.UserDataType;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.strategies.HitboxStrategy;

/**
 * This strategy makes the attached hbox deflect other hboxes it hits.
 * Deflecting just changes the momentum of the other hbox.
 * @author Brolackett Borbobitha
 */
public class ContactBlockProjectiles extends HitboxStrategy {
	
	//this is the knockback that should be administered to the other hbox
	private final float knockback;
	
	public ContactBlockProjectiles(PlayState state, Hitbox proj, BodyData user, float kb) {
		super(state, proj, user);
		this.knockback = kb;
	}
	
	@Override
	public void onHit(HadalData fixB) {
		if (fixB != null) {
			if (fixB.getType().equals(UserDataType.HITBOX)) {
				fixB.receiveDamage(0, hbox.getLinearVelocity().nor().scl(knockback), creator, false,
						hbox, DamageTypes.DEFLECT);
			}
		}
	}
}
