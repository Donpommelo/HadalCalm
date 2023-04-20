package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.StatusComposite;
import com.mygdx.hadal.constants.Stats;

public class SirenChime extends Artifact {

	private static final int SLOT_COST = 1;
	
	private static final float KNOCKBACK_REDUCTION = -2.5f;
	
	public SirenChime() {
		super(SLOT_COST);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new StatusComposite(state, p,
				new StatChangeStatus(state, Stats.KNOCKBACK_AMP, KNOCKBACK_REDUCTION, p));
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) -(KNOCKBACK_REDUCTION * 100))};
	}
}
