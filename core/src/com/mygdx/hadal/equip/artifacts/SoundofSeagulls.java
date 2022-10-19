package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.StatusComposite;
import com.mygdx.hadal.constants.Stats;

public class SoundofSeagulls extends Artifact {

	private static final int slotCost = 1;

	private static final float hoverCostReduction = -0.2f;

	public SoundofSeagulls() {
		super(slotCost);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new StatusComposite(state, p,
				new StatChangeStatus(state, Stats.HOVER_CONTROL, 1.0f, p),
				new StatChangeStatus(state, Stats.HOVER_COST, hoverCostReduction, p));
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) -(hoverCostReduction * 100))};
	}
}
