package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.StatusComposite;
import com.mygdx.hadal.constants.Stats;

public class BackpackBuddy extends Artifact {

	private static final int SLOT_COST = 0;
	
	private static final float HP_REDUCTION = -0.25f;
	private static final int BONUS_ARTIFACT_SLOTS = 1;
	
	public BackpackBuddy() {
		super(SLOT_COST);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new StatusComposite(state, p,
				new StatChangeStatus(state, Stats.MAX_HP_PERCENT, HP_REDUCTION, p),
				new StatChangeStatus(state, Stats.ARTIFACT_SLOTS, BONUS_ARTIFACT_SLOTS, p));
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) -(HP_REDUCTION * 100))};
	}
}
