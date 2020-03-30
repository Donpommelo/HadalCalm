package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.utils.Stats;

public class RecyclerBolus extends Artifact {

	private final static int statusNum = 1;
	private final static int slotCost = 2;
	
	private final float hpBuff = 1.0f;
	
	public RecyclerBolus() {
		super(slotCost, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new Status(state, b) {
			
			private float bonusHp;
			
			@Override
			public void scrapPickup() {
				bonusHp += hpBuff;
				inflicted.calcStats();
			}
			
			@Override
			public void onDeath(BodyData perp) {
				bonusHp = 0;
				inflicted.calcStats();
			}
			
			@Override
			public void statChanges() {
				inflicted.setStat(Stats.MAX_HP, inflicted.getStat(Stats.MAX_HP) + bonusHp);
			}
		};
		return enchantment;
	}
}
