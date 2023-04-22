package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.StatusComposite;
import com.mygdx.hadal.constants.Stats;

public class SwordofSyzygy extends Artifact {

	private static final int SLOT_COST = 1;
	
	private static final float BONUS_PROJ_DURABILITY = 3.0f;
	private static final float BONUS_DAMAGE_AMP = 0.15f;
	
	public SwordofSyzygy() {
		super(SLOT_COST);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new StatusComposite(state, p,
				new StatChangeStatus(state, Stats.RANGED_PROJ_DURABILITY, BONUS_PROJ_DURABILITY, p),
				new StatChangeStatus(state, Stats.DAMAGE_AMP, BONUS_DAMAGE_AMP, p));
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) BONUS_PROJ_DURABILITY),
				String.valueOf((int) (BONUS_DAMAGE_AMP * 100))};
	}
}
