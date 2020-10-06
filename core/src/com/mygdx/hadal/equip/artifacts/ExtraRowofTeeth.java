package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.StatusComposite;
import com.mygdx.hadal.utils.Stats;

public class ExtraRowofTeeth extends Artifact {

	private static final int statusNum = 1;
	private static final int slotCost = 2;
	
	private static final float bonusClipSize = 0.3f;
	
	public ExtraRowofTeeth() {
		super(slotCost, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new StatusComposite(state, b, new StatChangeStatus(state, Stats.RANGED_CLIP, bonusClipSize, b));
		return enchantment;
	}
}
