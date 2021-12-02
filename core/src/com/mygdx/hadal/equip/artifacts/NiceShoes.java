package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.StatusComposite;
import com.mygdx.hadal.utils.Stats;

public class NiceShoes extends Artifact {

	private static final int slotCost = 2;
	
	private static final float bonusSpd = 0.25f;
	private static final float bonusAccel = 0.25f;
	
	public NiceShoes() {
		super(slotCost);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new StatusComposite(state, p,
				new StatChangeStatus(state, Stats.GROUND_SPD, bonusSpd, p),
				new StatChangeStatus(state, Stats.GROUND_ACCEL, bonusAccel, p));
	}
}
