package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.StatusComposite;
import com.mygdx.hadal.utils.Stats;

public class TempestTeapot extends Artifact {

	private final static int statusNum = 1;
	private final static int slotCost = 1;
	
	private static final float bonusBoostSize = 0.5f;
	private static final float bonusBoostPow = 0.5f;
	
	public TempestTeapot() {
		super(slotCost, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new StatusComposite(state, b, 
				new StatChangeStatus(state, Stats.BOOST_POW, bonusBoostPow, b),
				new StatChangeStatus(state, Stats.BOOST_SIZE, bonusBoostSize, b)
		);
		return enchantment;
	}
}
