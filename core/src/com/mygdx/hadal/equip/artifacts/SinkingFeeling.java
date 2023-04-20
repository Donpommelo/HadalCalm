package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.constants.Stats;

public class SinkingFeeling extends Artifact {

	private static final int SLOT_COST = 1;
	
	private static final float BONUS_FAST_FALL = 1.1f;
	
	public SinkingFeeling() {
		super(SLOT_COST);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new StatChangeStatus(state, Stats.FASTFALL_POW, BONUS_FAST_FALL, p);
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) (BONUS_FAST_FALL * 100))};
	}
}
