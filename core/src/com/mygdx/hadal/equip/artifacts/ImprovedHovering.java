package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.Status;

public class ImprovedHovering extends Artifact {

	static String name = "Improved Hovering";
	public Status[] enchantment = new Status[2];
	
	public ImprovedHovering() {
		super(name);
	}

	public Status[] getEnchantment(BodyData b) {
		enchantment[0] = new StatChangeStatus(12, 0.25f, b, b, 50);
		enchantment[1] = new StatChangeStatus(13, -0.25f, b, b, 50);
		return enchantment;
	}
}
