package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.StatusComposite;
import com.mygdx.hadal.constants.Stats;

public class TempestTeapot extends Artifact {

	private static final int SLOT_COST = 1;
	
	private static final float BONUS_BOOST_SIZE = 0.75f;
	private static final float BONUS_BOOST_POW = 0.5f;
	
	public TempestTeapot() {
		super(SLOT_COST);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new StatusComposite(state, p,
				new StatChangeStatus(state, Stats.BOOST_POW, BONUS_BOOST_POW, p),
				new StatChangeStatus(state, Stats.BOOST_SIZE, BONUS_BOOST_SIZE, p)
		);
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) (BONUS_BOOST_POW * 100)),
				String.valueOf((int) (BONUS_BOOST_SIZE * 100))};
	}
}
