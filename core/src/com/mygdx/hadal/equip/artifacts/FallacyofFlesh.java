package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.constants.Stats;

public class FallacyofFlesh extends Artifact {

	private static final int SLOT_COST = 3;

	private static final float BONUS_HP = 0.7f;
	
	public FallacyofFlesh() {
		super(SLOT_COST);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new StatChangeStatus(state, Stats.MAX_HP_PERCENT, BONUS_HP, p);
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) (BONUS_HP * 100))};
	}
}
