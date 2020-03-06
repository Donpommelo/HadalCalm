package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.StatusComposite;
import com.mygdx.hadal.utils.Stats;

public class SeafoamTalisman extends Artifact {

	private final static int statusNum = 1;
	private final static int slotCost = 1;
	
	private static float bonusAtkSpd = 0.2f;
	private static float bonusReloadSpd = 0.2f;
	private static float gravityReduction = -3.0f;
	
	public SeafoamTalisman() {
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
