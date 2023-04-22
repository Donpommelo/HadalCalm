package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.StatusComposite;
import com.mygdx.hadal.constants.Stats;

public class CompoundVitreous extends Artifact {

	private static final int SLOT_COST = 1;

	private static final float HP_VISIBILITY = 1.0f;
	private static final float VISION_BONUS = 0.3f;

	public CompoundVitreous() {
		super(SLOT_COST);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new StatusComposite(state, p,
				new StatChangeStatus(state, Stats.VISION_RADIUS, VISION_BONUS, p),
				new StatChangeStatus(state, Stats.HEALTH_VISIBILITY, HP_VISIBILITY, p));
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) (VISION_BONUS * 100))};
	}
}
