package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.StatusComposite;
import com.mygdx.hadal.constants.Stats;

public class ExtraRowofTeeth extends Artifact {

	private static final int SLOT_COST = 2;
	
	private static final float BONUS_CLIP_SIZE = 0.3f;
	
	public ExtraRowofTeeth() {
		super(SLOT_COST);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new StatusComposite(state, p, new StatChangeStatus(state, Stats.RANGED_CLIP, BONUS_CLIP_SIZE, p));
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) (BONUS_CLIP_SIZE * 100))};
	}
}
