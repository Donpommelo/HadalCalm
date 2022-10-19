package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.StatusComposite;
import com.mygdx.hadal.constants.Stats;

public class SwordofSyzygy extends Artifact {

	private static final int slotCost = 1;
	
	private static final float bonusProjDurability = 3.0f;
	private static final float bonusDamageAmp = 0.15f;
	
	public SwordofSyzygy() {
		super(slotCost);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new StatusComposite(state, p,
				new StatChangeStatus(state, Stats.RANGED_PROJ_DURABILITY, bonusProjDurability, p),
				new StatChangeStatus(state, Stats.DAMAGE_AMP, bonusDamageAmp, p));
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) bonusProjDurability),
				String.valueOf((int) (bonusDamageAmp * 100))};
	}
}
