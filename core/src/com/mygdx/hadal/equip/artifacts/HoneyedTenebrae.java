package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.statuses.Status;

import java.util.Arrays;

public class HoneyedTenebrae extends Artifact {

	private static final int statusNum = 1;
	private static final int slotCost = 1;

	private static final float res = 0.3f;

	public HoneyedTenebrae() {
		super(slotCost, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new Status(state, b) {
			
			@Override
			public float onReceiveDamage(float damage, BodyData perp, DamageTypes... tags) {
				if (Arrays.asList(tags).contains(DamageTypes.BEES)) {
					return damage * res;
				}
				return damage;
			}
		};
		return enchantment;
	}
}
