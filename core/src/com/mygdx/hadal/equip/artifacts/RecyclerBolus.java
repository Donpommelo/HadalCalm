package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.utils.Stats;

public class RecyclerBolus extends Artifact {

	private static final int slotCost = 3;
	
	private final float hpBuff = 0.05f;
	private final int maxStacks = 30;
	
	public RecyclerBolus() {
		super(slotCost);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new Status(state, p) {
			
			private int currentStacks;
			@Override
			public void scrapPickup() {
				if (currentStacks < maxStacks) {
					currentStacks++;
					p.calcStats();
				}
			}
			
			@Override
			public void onDeath(BodyData perp) {
				currentStacks = 0;
				p.calcStats();
			}
			
			@Override
			public void statChanges() {
				p.setStat(Stats.MAX_HP, p.getStat(Stats.MAX_HP_PERCENT) + currentStacks * hpBuff);
			}
		};
	}
}
