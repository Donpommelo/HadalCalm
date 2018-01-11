package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.Status;

public class ImprovedPropulsion extends Artifact {

	static String name = "Improved Propulsion";
	public Status[] enchantment = new Status[1];
	
	public ImprovedPropulsion() {
		super(name);
	}

	public Status[] getEnchantment(BodyData b) {
		enchantment[0] = new StatChangeStatus(15, -0.25f, b, b, 50);
		return enchantment;
	}
}
