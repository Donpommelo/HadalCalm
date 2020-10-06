package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;

public class ClawsofFestus extends Artifact {

	private static final int statusNum = 1;
	private static final int slotCost = 1;
	
	public ClawsofFestus() {
		super(slotCost, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new Status(state, b) {
			
			@Override
			public void onInflict() {
				((Player) inflicted.getSchmuck()).setScaling(true);
			}
			
			@Override
			public void onRemove() {
				((Player) inflicted.getSchmuck()).setScaling(false);
			}
		};
		return enchantment;
	}
}
