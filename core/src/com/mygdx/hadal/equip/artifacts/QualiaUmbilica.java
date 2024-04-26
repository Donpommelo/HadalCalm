package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.constants.Stats;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;

public class QualiaUmbilica extends Artifact {

	private static final int SLOT_COST = 1;

	private static final float RESPAWN_TIME_REDUCTION = -0.75f;

	public QualiaUmbilica() {
		super(SLOT_COST);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new StatChangeStatus(state, Stats.RESPAWN_TIME, RESPAWN_TIME_REDUCTION, p);
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) (-RESPAWN_TIME_REDUCTION * 100))};
	}
}
