package com.mygdx.hadal.schmucks.strategies;

import com.mygdx.hadal.equip.Equipable;
import com.mygdx.hadal.schmucks.UserDataTypes;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;

public class HitboxOnContactBlockProjectilesStrategy extends HitboxStrategy{
	
	private Equipable tool;
	private float knockback;
	
	public HitboxOnContactBlockProjectilesStrategy(PlayState state, Hitbox proj, BodyData user, Equipable tool, float kb) {
		super(state, proj, user);
		this. tool = tool;
		this.knockback = kb;
	}
	
	@Override
	public void onHit(HadalData fixB) {
		if (fixB != null) {
			if (fixB.getType().equals(UserDataTypes.HITBOX)){
				fixB.receiveDamage(0, hbox.getLinearVelocity().nor().scl(knockback), creator, tool, true, DamageTypes.DEFLECT, DamageTypes.REFLECT);
			}
		}
	}
}
