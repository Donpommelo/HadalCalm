package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;

public class Artifact {

	protected Status enchantment;
	private final int slotCost;
	
	public Artifact(int slotCost) {
		this.slotCost = slotCost;
	}
	
	public void loadEnchantments(PlayState state, PlayerBodyData p) {}

	public Status getEnchantment() { return enchantment; }

	public int getSlotCost() { return slotCost; }

	/**
	 * These fields represent the item's stats to appear in its description
	 */
	public String[] getDescFields() { return new String[] {}; }
}
