package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.StatusComposite;
import com.mygdx.hadal.utils.Stats;

public class DayattheFair extends Artifact {

	private static final int statusNum = 1;
	private static final int slotCost = 1;

	private static final float bonusKnockbackRes = -1.5f;
	private static final int bonusHp = 25;

	public DayattheFair() {
		super(slotCost, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new StatusComposite(state, b,
			new StatChangeStatus(state, Stats.KNOCKBACK_RES, bonusKnockbackRes, b),
			new StatChangeStatus(state, Stats.MAX_HP, bonusHp, b));
		return enchantment;
	}
}
