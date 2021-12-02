package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.StatusComposite;
import com.mygdx.hadal.utils.Stats;

public class IronSights extends Artifact {

	private static final int slotCost = 1;
	private static final float recoilReduction = -1.0f;
	private static final float bonusPrjSpd = 0.25f;
	
	public IronSights() {
		super(slotCost);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new StatusComposite(state, p,
				new StatChangeStatus(state, Stats.RANGED_RECOIL, recoilReduction, p),
				new StatChangeStatus(state, Stats.RANGED_PROJ_SPD, bonusPrjSpd, p));
	}
}
