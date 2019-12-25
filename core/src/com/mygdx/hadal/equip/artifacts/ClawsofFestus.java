package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;

public class ClawsofFestus extends Artifact {

	private final static int statusNum = 1;
	private final static int slotCost = 1;
	
	public ClawsofFestus() {
		super(slotCost, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new Status(state, b) {
			
			@Override
			public void onInflict(Status s) {
				if (s.equals(this)) {
					inflicted.getSchmuck().setScaling(true);
				}
			}
			
			@Override
			public void onRemove(Status s) {
				if (s.equals(this)) {
					inflicted.getSchmuck().setScaling(false);
				}
			}
		};
		return enchantment;
	}
}
