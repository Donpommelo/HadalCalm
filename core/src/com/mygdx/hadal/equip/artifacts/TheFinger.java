package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.utils.Stats;

public class TheFinger extends Artifact {

	private static final int statusNum = 1;
	private static final int slotCost = 1;
	
	private static final int pingDamage = 1;
	
	public TheFinger() {
		super(slotCost, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new StatChangeStatus(state, Stats.PING_DAMAGE, pingDamage, b);
		return enchantment;
	}
}
