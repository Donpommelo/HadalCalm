package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.StatusComposite;
import com.mygdx.hadal.utils.Stats;

public class FensClippedWings extends Artifact {

	private static final int slotCost = 2;
	private static final int bonusJumpNum = 1;
	private static final float bonusJumpPow = 0.2f;
	
	public FensClippedWings() {
		super(slotCost);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new StatusComposite(state, p,
				new StatChangeStatus(state, Stats.JUMP_POW, bonusJumpPow, p),
				new StatChangeStatus(state, Stats.JUMP_NUM, bonusJumpNum, p));
	}
}
