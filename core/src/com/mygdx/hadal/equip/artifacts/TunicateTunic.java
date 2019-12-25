package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.StatusComposite;
import com.mygdx.hadal.utils.Stats;

public class TunicateTunic extends Artifact {

	private final static int statusNum = 1;
	private final static int slotCost = 1;
	
	public TunicateTunic() {
		super(slotCost, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new StatusComposite(state, b, new StatChangeStatus(state, Stats.KNOCKBACK_RES, 0.75f, b));
		return enchantment;
	}
}
