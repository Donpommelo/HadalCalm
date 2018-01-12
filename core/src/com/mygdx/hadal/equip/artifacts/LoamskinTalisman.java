package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.Status;

public class LoamskinTalisman extends Artifact {

	static String name = "Loamskin Talisman";
	static String descr = "+1 Hp Regen";
	static String descrLong = "";
	public Status[] enchantment = new Status[1];
	
	public LoamskinTalisman() {
		super(name, descr, descrLong);
	}

	public Status[] getEnchantment(BodyData b) {
		enchantment[0] = new StatChangeStatus(2, 1.0f, b, b, 50);
		return enchantment;
	}
}
