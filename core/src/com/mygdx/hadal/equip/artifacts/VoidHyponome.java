package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.StatusComposite;
import com.mygdx.hadal.constants.Stats;

public class VoidHyponome extends Artifact {

	private static final int SLOT_COST = 2;
	
	private static final float BOOST_COST_REDUCTION = -0.25f;
	
	public VoidHyponome() {
		super(SLOT_COST);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new StatusComposite(state, p, new StatChangeStatus(state, Stats.BOOST_COST, BOOST_COST_REDUCTION, p));
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) -(BOOST_COST_REDUCTION * 100))};
	}
}
