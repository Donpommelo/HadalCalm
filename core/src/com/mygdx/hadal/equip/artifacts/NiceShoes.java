package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.Status;

public class NiceShoes extends Artifact {

	static String name = "Nice Shoes";
	public Status[] enchantment = new Status[1];
	
	public NiceShoes() {
		super(name);
	}

	public Status[] getEnchantment(BodyData b) {
		enchantment[0] = new StatChangeStatus(4, 0.40f, b, b, 50);
		return enchantment;
	}
}
