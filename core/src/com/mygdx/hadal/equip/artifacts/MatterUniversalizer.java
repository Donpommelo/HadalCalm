package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.statuses.Status;

public class MatterUniversalizer extends Artifact {

	private final static String name = "Matter Universalizer";
	private final static String descr = "Inflicting damage regenerates Fuel.";
	private final static String descrLong = "";
	private final static int statusNum = 1;
	
	private final float amount = 0.4f;
	
	public MatterUniversalizer() {
		super(name, descr, descrLong, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new Status(state, name, descr, b) {
			
			@Override
			public float onDealDamage(float damage, BodyData vic, DamageTypes... tags) {

				((PlayerBodyData)inflicted).fuelGain(damage * amount);
				
				return damage;
			}
		};
		return enchantment;
	}
}
