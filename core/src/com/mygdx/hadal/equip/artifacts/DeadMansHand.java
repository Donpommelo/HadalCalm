package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.utils.Stats;

public class DeadMansHand extends Artifact {

	private final static int statusNum = 1;
	private final static int slotCost = 1;
	
	private final static int bonusSlots = 1;
	
	public DeadMansHand() {
		super(slotCost, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new StatChangeStatus(state, Stats.WEAPON_SLOTS, bonusSlots, b) {
			
			@Override
			public void onInflict(Status s) {
				if (inflicted instanceof PlayerBodyData && s.equals(this)) {
					((PlayerBodyData)inflicted).emptySlot(3);
				}
			}
		};		
		return enchantment;
	}
}
