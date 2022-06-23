package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.StatusComposite;
import com.mygdx.hadal.utils.Stats;

public class TunicateTunic extends Artifact {

	private static final int slotCost = 1;
	
	private static final float bonusKnockbackRes = 0.75f;
	private static final float bonusHp = 0.1f;

	public TunicateTunic() {
		super(slotCost);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new StatusComposite(state, p,
			new StatChangeStatus(state, Stats.KNOCKBACK_RES, bonusKnockbackRes, p),
			new StatChangeStatus(state, Stats.MAX_HP_PERCENT, bonusHp, p));
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) (bonusKnockbackRes * 100)),
				String.valueOf((int) (bonusHp * 100))};
	}
}
