package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.utils.Stats;

public class FeelingofBeingWatched extends Artifact {

	private final static int statusNum = 1;
	private final static int slotCost = 1;
	
	private final static float fuelThreshold = 0.2f;
	private final static float bonusDamage = 1.5f;
	
	public FeelingofBeingWatched() {
		super(slotCost, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new Status(state, b) {
			
			@Override
			public float onDealDamage(float damage, BodyData vic, DamageTypes... tags) {
				
				if (inflicter.getCurrentFuel() <= inflicter.getStat(Stats.MAX_FUEL) * fuelThreshold) {
					return damage * bonusDamage;
				}
				return damage;
			}
		};
		return enchantment;
	}
}
