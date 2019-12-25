package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.StatusComposite;
import com.mygdx.hadal.utils.Stats;

public class MoonFluther extends Artifact {

	private final static int statusNum = 1;
	private final static int slotCost = 2;
	
	public MoonFluther() {
		super(slotCost, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new StatusComposite(state, b, 
				new StatChangeStatus(state, Stats.HOVER_POW, 0.25f, b), 
				new StatChangeStatus(state, Stats.HOVER_COST, -0.25f, b));
		return enchantment;
	}
}
