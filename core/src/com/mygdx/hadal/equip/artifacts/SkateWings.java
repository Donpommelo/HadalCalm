package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.Status;

public class SkateWings extends Artifact {

	static String name = "Skate Wings";
	public Status[] enchantment = new Status[2];
	
	public SkateWings() {
		super(name);
	}

	public Status[] getEnchantment(BodyData b) {
		enchantment[0] = new StatChangeStatus(10, 0.2f, b, b, 50);
		enchantment[1] = new StatChangeStatus(11, 1, b, b, 50);
		return enchantment;
	}
}
