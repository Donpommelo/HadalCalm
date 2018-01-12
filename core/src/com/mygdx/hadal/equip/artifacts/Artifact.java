package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.statuses.Status;

public class Artifact {

	
	public String name, descr, descrLong;
	public Status[] statuses;
	
	public Artifact(String name, String descr, String descrLong) {
		this.name = name;
		this.descr = descr;
		this.descrLong = descrLong;
	}
	
	public Status[] getEnchantment(BodyData b) {
		return null;
	}
}
