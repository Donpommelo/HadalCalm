package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.StatusComposite;
import com.mygdx.hadal.constants.Stats;

public class NiceShoes extends Artifact {

	private static final int SLOT_COST = 2;
	
	private static final float BONUS_SPD = 0.25f;
	private static final float BONUS_ACCEL = 0.25f;
	
	public NiceShoes() {
		super(SLOT_COST);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new StatusComposite(state, p,
				new StatChangeStatus(state, Stats.GROUND_SPD, BONUS_SPD, p),
				new StatChangeStatus(state, Stats.GROUND_ACCEL, BONUS_ACCEL, p));
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) (BONUS_SPD * 100)),
				String.valueOf((int) (BONUS_ACCEL * 100))};
	}
}
