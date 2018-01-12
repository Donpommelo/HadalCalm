package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.Status;

public class GoodHealth extends Artifact {

	static String name = "Good Health";
	static String descr = "+25 Hp";
	static String descrLong = "";
	public Status[] enchantment = new Status[1];
	
	public GoodHealth() {
		super(name, descr, descrLong);
	}

	public Status[] getEnchantment(BodyData b) {
		enchantment[0] = new StatChangeStatus(0, 20, b, b, 50);
		return enchantment;
	}
}
