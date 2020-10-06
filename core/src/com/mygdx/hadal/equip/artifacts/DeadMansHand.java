package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.utils.Stats;

public class DeadMansHand extends Artifact {

	private static final int statusNum = 1;
	private static final int slotCost = 1;
	
	private static final int bonusSlots = 1;
	
	public DeadMansHand() {
		super(slotCost, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new StatChangeStatus(state, Stats.WEAPON_SLOTS, bonusSlots, b);		
		return enchantment;
	}
}
