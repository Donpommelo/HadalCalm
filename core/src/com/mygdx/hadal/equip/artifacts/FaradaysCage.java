package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.statuses.Status;

public class FaradaysCage extends Artifact {

	private static final int statusNum = 1;
	private static final int slotCost = 2;
	
	private final float amount = 0.5f;
	
	public FaradaysCage() {
		super(slotCost, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new Status(state, b) {
			
			@Override
			public float onReceiveDamage(float damage, BodyData perp, DamageTypes... tags) {				
				if (inflicted instanceof PlayerBodyData playerData && damage > 0) {
					float amountReduced = damage * amount;
					if (inflicted.getCurrentFuel() >= amountReduced) {
						playerData.fuelSpend(amountReduced);
						return damage - amountReduced;
					} else {
						float newDamage = damage - inflicted.getCurrentFuel();
						playerData.fuelSpend(inflicted.getCurrentFuel());
						return newDamage;
					}
				}
				return damage;
			}
		};
		return enchantment;
	}
}
