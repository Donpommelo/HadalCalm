package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.StatusComposite;
import com.mygdx.hadal.utils.Stats;

public class SeafoamPeriapt extends Artifact {

	private static final int slotCost = 1;
	
	private static final float bonusAtkSpd = 0.2f;
	private static final float bonusReloadSpd = 0.2f;
	private static final float gravityReduction = -3.0f;
	
	public SeafoamPeriapt() {
		super(slotCost);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new StatusComposite(state, p,
				new StatChangeStatus(state, Stats.RANGED_ATK_SPD, bonusAtkSpd, p),
				new StatChangeStatus(state, Stats.RANGED_RELOAD, bonusReloadSpd, p),
				new StatChangeStatus(state, Stats.RANGED_PROJ_GRAVITY, gravityReduction, p)
		);
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) (bonusReloadSpd * 100)),
				String.valueOf((int) (bonusAtkSpd * 100))};
	}
}
