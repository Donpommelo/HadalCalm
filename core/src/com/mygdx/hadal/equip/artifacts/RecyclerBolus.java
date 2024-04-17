package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.constants.Stats;

public class RecyclerBolus extends Artifact {

	private static final int SLOT_COST = 3;
	
	private static final float HP_BUFF = 0.05f;
	private static final int MAX_STACKS = 30;
	
	public RecyclerBolus() {
		super(SLOT_COST);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new Status(state, p) {
			
			private int currentStacks;
			@Override
			public void scrapPickup() {
				if (currentStacks < MAX_STACKS) {
					currentStacks++;
					p.calcStats();
				}
			}
			
			@Override
			public void onDeath(BodyData perp, DamageSource source) {
				currentStacks = 0;
				p.calcStats();
			}
			
			@Override
			public void statChanges() {
				p.setStat(Stats.MAX_HP_PERCENT, p.getStat(Stats.MAX_HP_PERCENT) + currentStacks * HP_BUFF);
			}
		};
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) (HP_BUFF * 100)),
				String.valueOf((int) (MAX_STACKS * HP_BUFF * 100))};
	}
}
