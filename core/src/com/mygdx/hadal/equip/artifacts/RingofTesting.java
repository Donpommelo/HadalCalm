package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.statuses.Lifesteal;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.TimedStatusTest;

public class RingofTesting extends Artifact {

	static String name = "Ring of Testing";
	static String descr = "Tests Things";
	static String descrLong = "";
	public Status[] enchantment = new Status[4];
	
	public RingofTesting() {
		super(name, descr, descrLong);
	}

	public Status[] getEnchantment(BodyData b) {
		enchantment[0] = new StatChangeStatus(35, 2.f, b, b, 50);
		enchantment[1] = new StatChangeStatus(42, 1.f, b, b, 50);
		enchantment[2] = new Lifesteal(0.05f, b, b, 50);
		enchantment[3] = new TimedStatusTest(b, b, 50);
		return enchantment;
	}
}
