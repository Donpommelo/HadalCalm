package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.StatusComposite;
import com.mygdx.hadal.constants.Stats;

public class FensClippedWings extends Artifact {

	private static final int SLOT_COST = 2;
	private static final int BONUS_JUMP_NUM = 1;
	private static final float BONUS_JUMP_POW = 0.2f;
	
	public FensClippedWings() {
		super(SLOT_COST);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new StatusComposite(state, p,
				new StatChangeStatus(state, Stats.JUMP_POW, BONUS_JUMP_POW, p),
				new StatChangeStatus(state, Stats.JUMP_NUM, BONUS_JUMP_NUM, p));
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) (BONUS_JUMP_POW * 100))};
	}
}
