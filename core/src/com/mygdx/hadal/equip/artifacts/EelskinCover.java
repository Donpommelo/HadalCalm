package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.Status;

public class EelskinCover extends Artifact {

	static String name = "Eelskin Cover";
	public Status[] enchantment = new Status[2];
	
	public EelskinCover() {
		super(name);
	}

	public Status[] getEnchantment(BodyData b) {
		enchantment[0] = new StatChangeStatus(8, -0.40f, b, b, 50);
		enchantment[1] = new StatChangeStatus(9, -0.40f, b, b, 50);
		return enchantment;
	}
}
