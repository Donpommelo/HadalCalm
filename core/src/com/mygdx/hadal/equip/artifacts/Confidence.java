package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.utils.Stats;

public class Confidence extends Artifact {

	private final static String name = "Confidence";
	private final static String descr = "+50% damage at Max Hp.";
	private final static String descrLong = "";
	private final static int statusNum = 1;
	
	private final static float damageBoost = 1.5f;

	public Confidence() {
		super(name, descr, descrLong, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new Status(state, name, descr, b) {
			
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
