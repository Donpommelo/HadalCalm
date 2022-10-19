package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.StatusComposite;
import com.mygdx.hadal.constants.Stats;

public class MoonFluther extends Artifact {

	private static final int slotCost = 2;
	
	private static final float bonusHoverPow = 0.25f;
	private static final float hoverCostReduction = -0.25f;
	
	public MoonFluther() {
		super(slotCost);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new StatusComposite(state, p,
				new StatChangeStatus(state, Stats.HOVER_POW, bonusHoverPow, p),
				new StatChangeStatus(state, Stats.HOVER_COST, hoverCostReduction, p));
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) (bonusHoverPow * 100)),
				String.valueOf((int) -(hoverCostReduction * 100))};
	}
}
