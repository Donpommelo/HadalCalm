package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.utils.Stats;

public class SinkingFeeling extends Artifact {

	private static final int slotCost = 1;
	
	private static final float bonusFastFall = 1.0f;
	
	public SinkingFeeling() {
		super(slotCost);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new StatChangeStatus(state, Stats.FASTFALL_POW, bonusFastFall, p);
	}
}
