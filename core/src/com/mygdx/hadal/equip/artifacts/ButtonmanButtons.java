package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.utils.Stats;

public class ButtonmanButtons extends Artifact {

	private static final int slotCost = 1;
	
	private final float fuelRegenBuff = 3.0f;
	private final float reloadSpeedBuff = 0.1f;
	private final int maxStacks = 6;
	
	public ButtonmanButtons() {
		super(slotCost);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new Status(state, p) {
			
			private int currentStacks;
			@Override
			public void onKill(BodyData vic, DamageSource source) {
				if (vic instanceof PlayerBodyData) {
					if (currentStacks < maxStacks) {
						currentStacks++;
						p.calcStats();
					}
				}
			}
			
			@Override
			public void onDeath(BodyData perp, DamageSource source) {
				currentStacks = 0;
				p.calcStats();
			}
			
			@Override
			public void statChanges() {
				p.setStat(Stats.FUEL_REGEN, p.getStat(Stats.FUEL_REGEN) + currentStacks * fuelRegenBuff);
				p.setStat(Stats.RANGED_RELOAD, p.getStat(Stats.RANGED_RELOAD) + currentStacks * reloadSpeedBuff);
			}
		};
	}
}
