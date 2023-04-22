package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.StatusComposite;
import com.mygdx.hadal.constants.Stats;

public class MoonFluther extends Artifact {

	private static final int SLOT_COST = 2;
	
	private static final float BONUS_HOVER_POW = 0.25f;
	private static final float HOVER_COST_REDUCTION = -0.25f;
	
	public MoonFluther() {
		super(SLOT_COST);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new StatusComposite(state, p,
				new StatChangeStatus(state, Stats.HOVER_POW, BONUS_HOVER_POW, p),
				new StatChangeStatus(state, Stats.HOVER_COST, HOVER_COST_REDUCTION, p));
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) (BONUS_HOVER_POW * 100)),
				String.valueOf((int) -(HOVER_COST_REDUCTION * 100))};
	}
}
