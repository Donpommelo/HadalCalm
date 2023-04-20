package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.StatusComposite;
import com.mygdx.hadal.constants.Stats;

public class SoundofSeagulls extends Artifact {

	private static final int SLOT_COST = 1;

	private static final float HOVER_COST_REDUCTION = -0.2f;

	public SoundofSeagulls() {
		super(SLOT_COST);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new StatusComposite(state, p,
				new StatChangeStatus(state, Stats.HOVER_CONTROL, 1.0f, p),
				new StatChangeStatus(state, Stats.HOVER_COST, HOVER_COST_REDUCTION, p));
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) -(HOVER_COST_REDUCTION * 100))};
	}
}
