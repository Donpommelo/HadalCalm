package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.StatusComposite;
import com.mygdx.hadal.utils.Stats;

public class BackpackBuddy extends Artifact {

	private static final int slotCost = 0;
	
	private static final float hpReduction = -0.25f;
	private static final int bonusArtifactSlots = 1;
	
	public BackpackBuddy() {
		super(slotCost);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new StatusComposite(state, p,
				new StatChangeStatus(state, Stats.MAX_HP_PERCENT, hpReduction, p),
				new StatChangeStatus(state, Stats.ARTIFACT_SLOTS, bonusArtifactSlots, p));
	}
}
