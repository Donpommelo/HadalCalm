package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.statuses.Status;

public class FaradaysCage extends Artifact {

	private final static String name = "Faraday's Cage";
	private final static String descr = "Diverts incoming damage to Fuel.";
	private final static String descrLong = "";
	private final static int statusNum = 1;
	
	private final float amount = 0.4f;
	
	public FaradaysCage() {
		super(name, descr, descrLong, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new Status(state, name, descr, b) {
			
			@Override
			public float onReceiveDamage(float damage, BodyData perp, DamageTypes... tags) {				
				if (inflicted instanceof PlayerBodyData) {
					float amountReduced = damage * amount;
					if (inflicted.getCurrentFuel() >= amountReduced) {
						((PlayerBodyData)inflicted).fuelSpend(amountReduced);
						return damage - amountReduced;
					} else {
						float newDamage = damage - inflicted.getCurrentFuel();
						((PlayerBodyData)inflicted).fuelGain(inflicted.getCurrentFuel());
						return newDamage;
					}
				}
				return damage;
			}
		};;
		return enchantment;
	}
}
