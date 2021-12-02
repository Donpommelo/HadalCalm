package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.StatusComposite;
import com.mygdx.hadal.utils.Stats;

public class EelskinCover extends Artifact {

	private static final int slotCost = 1;
	
	private static final float groundDragReduction = -0.6f;
	private static final float airDragReduction = -0.4f;
	
	public EelskinCover() {
		super(slotCost);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new StatusComposite(state, p,
				new StatChangeStatus(state, Stats.AIR_DRAG, airDragReduction, p),
				new StatChangeStatus(state, Stats.GROUND_DRAG, groundDragReduction, p));
	}
}
