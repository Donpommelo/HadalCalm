package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.constants.Stats;

public class DeadMansHand extends Artifact {

	private static final int SLOT_COST = 1;
	
	private static final int BONUS_SLOTS = 1;
	
	public DeadMansHand() {
		super(SLOT_COST);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new StatChangeStatus(state, Stats.WEAPON_SLOTS, BONUS_SLOTS, p);
	}
}
