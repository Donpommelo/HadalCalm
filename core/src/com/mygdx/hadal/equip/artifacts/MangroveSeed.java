package com.mygdx.hadal.equip.artifacts;

import java.util.Arrays;

import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.statuses.Status;

public class MangroveSeed extends Artifact {

	private final static int statusNum = 1;
	private final static int slotCost = 1;
	
	private final static float res = 0.2f;
	
	public MangroveSeed() {
		super(slotCost, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new Status(state, b) {
			
			@Override
			public float onReceiveDamage(float damage, BodyData perp, DamageTypes... tags) {
				if (Arrays.asList(tags).contains(DamageTypes.POISON)) {
					return damage * res;
				}
				return damage;
			}
		};
		return enchantment;
	}
}
