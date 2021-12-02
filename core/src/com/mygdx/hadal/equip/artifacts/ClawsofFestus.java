package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;

public class ClawsofFestus extends Artifact {

	private static final int slotCost = 1;
	
	public ClawsofFestus() {
		super(slotCost);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new Status(state, p) {
			
			@Override
			public void onInflict() {
				p.getPlayer().setScaling(true);
			}
			
			@Override
			public void onRemove() { p.getPlayer().setScaling(false); }
		};
	}
}
