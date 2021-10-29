package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;

public class Artifact {

	protected final Status[] enchantment;
	private final int slotCost;
	
	public Artifact(int slotCost, int statusNum) {
		this.slotCost = slotCost;
		enchantment = new Status[statusNum];
	}
	
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		return null;
	}

	public int getSlotCost() { return slotCost; }
}
