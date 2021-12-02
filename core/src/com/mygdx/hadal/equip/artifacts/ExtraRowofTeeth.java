package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.StatusComposite;
import com.mygdx.hadal.utils.Stats;

public class ExtraRowofTeeth extends Artifact {

	private static final int slotCost = 2;
	
	private static final float bonusClipSize = 0.3f;
	
	public ExtraRowofTeeth() {
		super(slotCost);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new StatusComposite(state, p, new StatChangeStatus(state, Stats.RANGED_CLIP, bonusClipSize, p));
	}
}
