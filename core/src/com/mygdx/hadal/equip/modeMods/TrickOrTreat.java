package com.mygdx.hadal.equip.modeMods;

import com.mygdx.hadal.equip.artifacts.Artifact;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.TrickOrTreating;

public class TrickOrTreat extends Artifact {

	private static final int SLOT_COST = 0;

	public TrickOrTreat() { super(SLOT_COST); }

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new TrickOrTreating(state, p);
	}
}
