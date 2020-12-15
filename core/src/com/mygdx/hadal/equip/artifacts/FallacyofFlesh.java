package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.utils.Stats;

public class FallacyofFlesh extends Artifact {

	private static final int statusNum = 1;
	private static final int slotCost = 3;

	private static final float bonusHp = 0.7f;
	
	public FallacyofFlesh() {
		super(slotCost, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new StatChangeStatus(state, Stats.MAX_HP_PERCENT, bonusHp, b);
		return enchantment;
	}
}
