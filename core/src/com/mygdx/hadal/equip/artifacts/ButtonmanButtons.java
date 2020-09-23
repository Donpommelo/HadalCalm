package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.utils.Stats;

public class ButtonmanButtons extends Artifact {

	private final static int statusNum = 1;
	private final static int slotCost = 1;
	
	private final float fuelRegenBuff = 3.0f;
	private final float reloadSpeedBuff = 0.1f;
	private final int maxStacks = 6;
	
	public ButtonmanButtons() {
		super(slotCost, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new Status(state, b) {
			
			private int currentStacks;
			
			@Override
			public void onKill(BodyData vic) {
				if (vic instanceof PlayerBodyData) {
					if (currentStacks < maxStacks) {
						currentStacks++;
						inflicted.calcStats();
					}
				}
			}
			
			@Override
			public void onDeath(BodyData perp) {
				currentStacks = 0;
				inflicted.calcStats();
			}
			
			@Override
			public void statChanges() {
				inflicted.setStat(Stats.FUEL_REGEN, inflicted.getStat(Stats.FUEL_REGEN) + currentStacks * fuelRegenBuff);
				inflicted.setStat(Stats.RANGED_RELOAD, inflicted.getStat(Stats.RANGED_RELOAD) + currentStacks * reloadSpeedBuff);
			}
		};
		return enchantment;
	}
}
