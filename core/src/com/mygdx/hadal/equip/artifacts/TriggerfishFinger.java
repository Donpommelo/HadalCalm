package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.StatusComposite;
import com.mygdx.hadal.constants.Stats;

public class TriggerfishFinger extends Artifact {

	private static final int SLOT_COST = 2;
	
	private static final float BONUS_ATK_SPD = 0.2f;
	private static final float BONUS_RELOAD_SPD = 0.4f;
	
	public TriggerfishFinger() {
		super(SLOT_COST);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new StatusComposite(state, p,
				new StatChangeStatus(state, Stats.TOOL_SPD, BONUS_ATK_SPD, p),
				new StatChangeStatus(state, Stats.RANGED_RELOAD, BONUS_RELOAD_SPD, p));
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) (BONUS_ATK_SPD * 100)),
				String.valueOf((int) (BONUS_RELOAD_SPD * 100))};
	}
}
