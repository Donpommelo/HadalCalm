package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.statuses.Status;

public class AnchorTalisman extends Artifact {

	private static final int statusNum = 1;
	private static final int slotCost = 2;
	
	private static final float res = 0.4f;
	
	public AnchorTalisman() {
		super(slotCost, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new Status(state, b) {
			
			@Override
			public float onReceiveDamage(float damage, BodyData perp, DamageTypes... tags) {
				if (inflicted.getSchmuck().isGrounded() && damage > 0) {
					return damage * res;
				}
				return damage;
			}
		};
		return enchantment;
	}
}
