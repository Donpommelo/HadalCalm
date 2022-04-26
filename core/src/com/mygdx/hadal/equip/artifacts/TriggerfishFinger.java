package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.StatusComposite;
import com.mygdx.hadal.utils.Stats;

public class TriggerfishFinger extends Artifact {

	private static final int slotCost = 2;
	
	private static final float bonusAtkSpd = 0.2f;
	private static final float bonusReloadSpd = 0.4f;
	
	public TriggerfishFinger() {
		super(slotCost);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new StatusComposite(state, p,
				new StatChangeStatus(state, Stats.TOOL_SPD, bonusAtkSpd, p),
				new StatChangeStatus(state, Stats.RANGED_RELOAD, bonusReloadSpd, p));
	}
}
