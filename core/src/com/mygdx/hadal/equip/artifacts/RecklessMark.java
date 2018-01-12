package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.Status;

public class RecklessMark extends Artifact {

	static String name = "Reckless Mark";
	static String descr = "Deal and take +40% more damage.";
	static String descrLong = "";
	public Status[] enchantment = new Status[2];
	
	public RecklessMark() {
		super(name, descr, descrLong);
	}

	public Status[] getEnchantment(BodyData b) {
		enchantment[0] = new StatChangeStatus(21, 0.4f, b, b, 50);
		enchantment[1] = new StatChangeStatus(22, -0.4f, b, b, 50);
		return enchantment;
	}
}
