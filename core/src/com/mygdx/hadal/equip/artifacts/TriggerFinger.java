package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.Status;

public class TriggerFinger extends Artifact {

	static String name = "Trigger Finger";
	static String descr = "+30% Attack Speed";
	static String descrLong = "";
	public Status[] enchantment = new Status[1];
	
	public TriggerFinger() {
		super(name, descr, descrLong);
	}

	public Status[] getEnchantment(BodyData b) {
		enchantment[0] = new StatChangeStatus(25, 0.3f, b, b, 50);
		return enchantment;
	}
}
