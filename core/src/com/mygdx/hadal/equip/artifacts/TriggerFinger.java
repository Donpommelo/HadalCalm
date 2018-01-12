package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.Status;

public class TriggerFinger extends Artifact {

	static String name = "Trigger Finger";
	static String descr = "+30% Ranged Attack Speed";
	static String descrLong = "";
	public Status[] enchantment = new Status[1];
	
	public TriggerFinger() {
		super(name, descr, descrLong);
	}

	public Status[] getEnchantment(BodyData b) {
		enchantment[0] = new StatChangeStatus(27, 0.25f, b, b, 50);
		return enchantment;
	}
}
