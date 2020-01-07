package com.mygdx.hadal.schmucks.strategies;

import com.mygdx.hadal.schmucks.UserDataTypes;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;

/**
 * This strategy makes the attached hbox deflect other hboxes it hits.
 * Deflecting just changes the momentum of the other hbox.
 * @author Zachary Tu
 *
 */
public class HitboxOnContactBlockProjectilesStrategy extends HitboxStrategy{
	
	//this is the knockback that should be administered to the other hbox
	private float knockback;
	
	public HitboxOnContactBlockProjectilesStrategy(PlayState state, Hitbox proj, BodyData user, float kb) {
		super(state, proj, user);
		this.knockback = kb;
	}
	
	@Override
	public void onHit(HadalData fixB) {
		if (fixB != null) {
			if (fixB.getType().equals(UserDataTypes.HITBOX)){
				fixB.receiveDamage(0, hbox.getLinearVelocity().nor().scl(knockback), creator, true, DamageTypes.DEFLECT, DamageTypes.REFLECT);
			}
		}
	}
}
