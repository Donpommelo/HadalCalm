package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.utils.Stats;

public class FallacyofFlesh extends Artifact {

	private static final int slotCost = 3;

	private static final float bonusHp = 0.7f;
	
	public FallacyofFlesh() {
		super(slotCost);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new StatChangeStatus(state, Stats.MAX_HP_PERCENT, bonusHp, p);
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) (bonusHp * 100))};
	}
}
