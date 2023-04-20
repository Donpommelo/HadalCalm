package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.StatusComposite;
import com.mygdx.hadal.constants.Stats;

public class NacreousRudder extends Artifact {

	private static final int SLOT_COST = 1;

	private static final float BONUS_GROUND_ACCEL = 0.5f;
	private static final float BONUS_AIR_ACCEL = 0.5f;

	public NacreousRudder() {
		super(SLOT_COST);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new StatusComposite(state, p,
				new StatChangeStatus(state, Stats.AIR_ACCEL, BONUS_AIR_ACCEL, p),
				new StatChangeStatus(state, Stats.GROUND_ACCEL, BONUS_GROUND_ACCEL, p));
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) (BONUS_GROUND_ACCEL * 100)),
				String.valueOf((int) (BONUS_AIR_ACCEL * 100))};
	}
}
