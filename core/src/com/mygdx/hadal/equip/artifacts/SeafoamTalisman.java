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
	
	public SeafoamTalisman() {
		super(slotCost, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new StatusComposite(state, b, 
				new StatChangeStatus(state, Stats.RANGED_ATK_SPD, 0.20f, b),
				new StatChangeStatus(state, Stats.RANGED_RELOAD, 0.20f, b),
				new StatChangeStatus(state, Stats.RANGED_PROJ_GRAVITY, -5.0f, b)
		);
		return enchantment;
	}
}
