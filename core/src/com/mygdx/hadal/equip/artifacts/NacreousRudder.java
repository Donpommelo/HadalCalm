package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.StatusComposite;
import com.mygdx.hadal.utils.Stats;

public class NacreousRudder extends Artifact {

	private static final int slotCost = 1;

	private static final float bonusGroundAccel = 0.5f;
	private static final float bonusAirAccel = 0.5f;

	public NacreousRudder() {
		super(slotCost);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new StatusComposite(state, p,
				new StatChangeStatus(state, Stats.AIR_ACCEL, bonusAirAccel, p),
				new StatChangeStatus(state, Stats.GROUND_ACCEL, bonusGroundAccel, p));
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) (bonusGroundAccel * 100)),
				String.valueOf((int) (bonusAirAccel * 100))};
	}
}
