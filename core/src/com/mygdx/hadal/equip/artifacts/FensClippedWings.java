package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.StatusComposite;
import com.mygdx.hadal.utils.Stats;

public class FensClippedWings extends Artifact {

	private static final int statusNum = 1;
	private static final int slotCost = 2;
	
	private static final int bonusJumpNum = 1;
	private static final float bonusJumpPow = 0.2f;
	
	public FensClippedWings() {
		super(slotCost, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new StatusComposite(state, b, 
				new StatChangeStatus(state, Stats.JUMP_POW, bonusJumpPow, b), 
				new StatChangeStatus(state, Stats.JUMP_NUM, bonusJumpNum, b));
		return enchantment;
	}
}
