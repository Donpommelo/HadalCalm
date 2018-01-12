package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.Status;

public class NuclearPunchThrusters extends Artifact {

	static String name = "Nuclear Punch-THrusters";
	static String descr = "+50% Knockback";
	static String descrLong = "";
	public Status[] enchantment = new Status[1];
	
	public NuclearPunchThrusters() {
		super(name, descr, descrLong);
	}

	public Status[] getEnchantment(BodyData b) {
		enchantment[0] = new StatChangeStatus(23, 0.5f, b, b, 50);
		return enchantment;
	}
}
