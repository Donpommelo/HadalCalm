package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.constants.Stats;

public class ButtonmanButtons extends Artifact {

	private static final int SLOT_COST = 1;
	
	private static final float FUEL_REGEN_BUFF = 3.0f;
	private static final float RELOAD_SPEED_BUFF = 0.1f;
	private static final int MAX_STACKS = 8;
	
	public ButtonmanButtons() {
		super(SLOT_COST);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new Status(state, p) {
			
			private int currentStacks;
			@Override
			public void onKill(BodyData vic, DamageSource source, DamageTag... tags) {
				if (vic instanceof PlayerBodyData) {
					if (currentStacks < MAX_STACKS) {
						currentStacks++;
						p.calcStats();
					}
				}
			}
			
			@Override
			public void onDeath(BodyData perp, DamageSource source, DamageTag... tags) {
				currentStacks = 0;
				p.calcStats();
			}
			
			@Override
			public void statChanges() {
				p.setStat(Stats.FUEL_REGEN, p.getStat(Stats.FUEL_REGEN) + currentStacks * FUEL_REGEN_BUFF);
				p.setStat(Stats.RANGED_RELOAD, p.getStat(Stats.RANGED_RELOAD) + currentStacks * RELOAD_SPEED_BUFF);
			}
		};
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) FUEL_REGEN_BUFF),
				String.valueOf((int) (RELOAD_SPEED_BUFF * 100)),
				String.valueOf(MAX_STACKS)};
	}
}
