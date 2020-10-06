package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.StatusComposite;
import com.mygdx.hadal.utils.Stats;

public class SeafoamPeriapt extends Artifact {

	private static final int statusNum = 1;
	private static final int slotCost = 1;
	
	private static final float bonusAtkSpd = 0.2f;
	private static final float bonusReloadSpd = 0.2f;
	private static final float gravityReduction = -3.0f;
	
	public SeafoamPeriapt() {
		super(slotCost, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new StatusComposite(state, b, 
				new StatChangeStatus(state, Stats.RANGED_ATK_SPD, bonusAtkSpd, b),
				new StatChangeStatus(state, Stats.RANGED_RELOAD, bonusReloadSpd, b),
				new StatChangeStatus(state, Stats.RANGED_PROJ_GRAVITY, gravityReduction, b)
		);
		return enchantment;
	}
}
