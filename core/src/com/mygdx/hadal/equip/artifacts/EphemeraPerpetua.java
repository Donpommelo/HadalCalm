package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;

public class EphemeraPerpetua extends Artifact {

	private final static int statusNum = 1;
	private final static int slotCost = 2;
	
	private final float amount = 2.0f;
	
	public EphemeraPerpetua() {
		super(slotCost, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new Status(state, b) {
			
			@Override
			public void scrapPickup() {
				if (inflicted instanceof PlayerBodyData) {
					((PlayerBodyData) inflicted).getActiveItem().gainCharge(amount);
				}
			}
		};
		return enchantment;
	}
}
