package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.constants.Stats;

public class Plumpowder extends Artifact {

	private static final int slotCost = 1;
	
	public Plumpowder() {
		super(slotCost);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new StatChangeStatus(state, Stats.STARTING_CHARGE, 1.0f, p) {
			
			@Override
			public void playerCreate() {
				p.getActiveItem().setCurrentChargePercent(1.0f);
			}
		};
	}
}
