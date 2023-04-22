package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.StatusComposite;
import com.mygdx.hadal.constants.Stats;

public class IronSights extends Artifact {

	private static final int SLOT_COST = 1;

	private static final float RECOIL_REDUCTION = -1.0f;
	private static final float BONUS_PRJ_SPD = 0.25f;
	
	public IronSights() {
		super(SLOT_COST);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new StatusComposite(state, p,
				new StatChangeStatus(state, Stats.RANGED_RECOIL, RECOIL_REDUCTION, p),
				new StatChangeStatus(state, Stats.RANGED_PROJ_SPD, BONUS_PRJ_SPD, p));
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) -(RECOIL_REDUCTION * 100)),
				String.valueOf((int) (BONUS_PRJ_SPD * 100))};
	}
}
