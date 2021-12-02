package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.StatusComposite;
import com.mygdx.hadal.utils.Stats;

public class TempestTeapot extends Artifact {

	private static final int slotCost = 1;
	
	private static final float bonusBoostSize = 0.75f;
	private static final float bonusBoostPow = 0.5f;
	
	public TempestTeapot() {
		super(slotCost);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new StatusComposite(state, p,
				new StatChangeStatus(state, Stats.BOOST_POW, bonusBoostPow, p),
				new StatChangeStatus(state, Stats.BOOST_SIZE, bonusBoostSize, p)
		);
	}
}
