package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.Status;

public class NiceShoes extends Artifact {

	static String name = "Nice Shoes";
	static String descr = "+30% Ground Speed";
	static String descrLong = "";
	public Status[] enchantment = new Status[2];
	
	public NiceShoes() {
		super(name, descr, descrLong);
	}

	public Status[] getEnchantment(BodyData b) {
		enchantment[0] = new StatChangeStatus(4, 0.30f, b, b, 50);
		enchantment[1] = new StatChangeStatus(6, 0.50f, b, b, 50);
		return enchantment;
	}
}
