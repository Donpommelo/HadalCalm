package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.Status;

public class RootBoots extends Artifact {

	static String name = "Root-Boots";
	static String descr = "+75% Knockback Resistance";
	static String descrLong = "";
	public Status[] enchantment = new Status[1];
	
	public RootBoots() {
		super(name, descr, descrLong);
	}

	public Status[] getEnchantment(BodyData b) {
		enchantment[0] = new StatChangeStatus(24, 0.75f, b, b, 50);
		return enchantment;
	}
}
