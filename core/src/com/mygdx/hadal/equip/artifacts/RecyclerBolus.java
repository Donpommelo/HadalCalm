package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.utils.Stats;

public class RecyclerBolus extends Artifact {

	private static final int statusNum = 1;
	private static final int slotCost = 3;
	
	private final float hpBuff = 5.0f;
	private final int maxStacks = 30;
	
	public RecyclerBolus() {
		super(slotCost, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new Status(state, b) {
			
			private int currentStacks;
			
			@Override
			public void scrapPickup() {
				
				if (currentStacks < maxStacks) {
					currentStacks++;
					inflicted.calcStats();
				}
			}
			
			@Override
			public void onDeath(BodyData perp) {
				currentStacks = 0;
				inflicted.calcStats();
			}
			
			@Override
			public void statChanges() {
				inflicted.setStat(Stats.MAX_HP, inflicted.getStat(Stats.MAX_HP) + currentStacks * hpBuff);
			}
		};
		return enchantment;
	}
}
