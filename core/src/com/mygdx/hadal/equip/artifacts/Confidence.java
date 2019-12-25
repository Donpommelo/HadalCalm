package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.utils.Stats;

public class Confidence extends Artifact {

	private final static int statusNum = 1;
	private final static int slotCost = 2;
	
	private final static float damageBoost = 1.5f;

	public Confidence() {
		super(slotCost, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new Status(state, b) {
			
			@Override
			public float onDealDamage(float damage, BodyData vic, DamageTypes... tags) { 
				if (inflicter.getCurrentHp() == inflicter.getStat(Stats.MAX_HP)) {
					return damage * damageBoost;
				}
				return damage;	
			}
			
		};
		return enchantment;
	}
}
