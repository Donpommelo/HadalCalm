package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.StatusComposite;
import com.mygdx.hadal.constants.Stats;

public class TunicateTunic extends Artifact {

	private static final int SLOT_COST = 1;
	
	private static final float BONUS_KNOCKBACK_RES = 0.75f;
	private static final float BONUS_HP = 0.1f;

	public TunicateTunic() {
		super(SLOT_COST);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new StatusComposite(state, p,
			new StatChangeStatus(state, Stats.KNOCKBACK_RES, BONUS_KNOCKBACK_RES, p),
			new StatChangeStatus(state, Stats.MAX_HP_PERCENT, BONUS_HP, p));
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) (BONUS_KNOCKBACK_RES * 100)),
				String.valueOf((int) (BONUS_HP * 100))};
	}
}
