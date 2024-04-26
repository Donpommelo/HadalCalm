package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.constants.Stats;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.StatusComposite;

public class BroochOfBetterDays extends Artifact {

	private static final int SLOT_COST = 1;

	private static final float BONUS_ALL_STATS = 0.05f;

	public BroochOfBetterDays() {
		super(SLOT_COST);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new StatusComposite(state, p,
				new StatChangeStatus(state, Stats.MAX_HP_PERCENT, BONUS_ALL_STATS, p),
				new StatChangeStatus(state, Stats.GROUND_SPD, BONUS_ALL_STATS, p),
				new StatChangeStatus(state, Stats.AIR_SPD, BONUS_ALL_STATS, p),
				new StatChangeStatus(state, Stats.GROUND_ACCEL, BONUS_ALL_STATS, p),
				new StatChangeStatus(state, Stats.AIR_ACCEL, BONUS_ALL_STATS, p),
				new StatChangeStatus(state, Stats.ACTIVE_CHARGE_RATE, BONUS_ALL_STATS, p),
				new StatChangeStatus(state, Stats.TOOL_SPD, BONUS_ALL_STATS, p),
				new StatChangeStatus(state, Stats.RANGED_RELOAD, BONUS_ALL_STATS, p));
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) (BONUS_ALL_STATS * 100))};
	}
}
