package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.StatusComposite;
import com.mygdx.hadal.utils.Stats;

public class IronSights extends Artifact {

	private static final int statusNum = 1;
	private static final int slotCost = 1;
	
	private static final float recoilReduction = -1.0f;
	private static final float bonusPrjSpd = 0.25f;
	
	public IronSights() {
		super(slotCost, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new StatusComposite(state, b, 
				new StatChangeStatus(state, Stats.RANGED_RECOIL, recoilReduction, b), 
				new StatChangeStatus(state, Stats.RANGED_PROJ_SPD, bonusPrjSpd, b));
		return enchantment;
	}
}
