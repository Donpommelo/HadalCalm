package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.statuses.Status;

public class Artifact {

	
	public String name;
	public Status[] statuses;
	
	public Artifact(String name) {
		this.name = name;
	}
	
	public Status[] getEnchantment(BodyData b) {
		return null;
	}
}
