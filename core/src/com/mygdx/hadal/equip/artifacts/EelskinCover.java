package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.StatusComposite;
import com.mygdx.hadal.constants.Stats;

public class EelskinCover extends Artifact {

	private static final int SLOT_COST = 1;
	
	private static final float GROUND_DRAG_REDUCTION = -0.6f;
	private static final float AIR_DRAG_REDUCTION = -0.4f;
	
	public EelskinCover() {
		super(SLOT_COST);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new StatusComposite(state, p,
				new StatChangeStatus(state, Stats.AIR_DRAG, AIR_DRAG_REDUCTION, p),
				new StatChangeStatus(state, Stats.GROUND_DRAG, GROUND_DRAG_REDUCTION, p));
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) -(GROUND_DRAG_REDUCTION * 100)),
				String.valueOf((int) -(AIR_DRAG_REDUCTION * 100))};
	}
}
