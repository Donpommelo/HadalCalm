package com.mygdx.hadal.equip.modeMods;

import com.mygdx.hadal.equip.artifacts.Artifact;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.StatusComposite;
import com.mygdx.hadal.utils.Stats;

public class VisibleHp extends Artifact {

	private static final int slotCost = 0;

	private static final float hpVisibility = 1.0f;

	public VisibleHp() { super(slotCost); }

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new StatusComposite(state, p,	new StatChangeStatus(state, Stats.HEALTH_VISIBILITY, hpVisibility, p));
	}
}
