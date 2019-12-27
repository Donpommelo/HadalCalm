package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.utils.Stats;

public class PainScale extends Artifact {

	private final static int statusNum = 1;
	private final static int slotCost = 2;
	
	private final float amount = 0.75f;
	
	public PainScale() {
		super(slotCost, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new Status(state, b) {
			
			@Override
			public float onReceiveDamage(float damage, BodyData perp, DamageTypes... tags) {
				if (inflicted instanceof PlayerBodyData) {
					((PlayerBodyData)inflicted).getActiveItem().gainChargeByPercent(damage / inflicted.getStat(Stats.MAX_HP) * amount);
				}
				return damage;
			}
		};
		return enchantment;
	}
}
