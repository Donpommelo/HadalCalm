package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;

public class Artifact {

	protected String name, descr, descrLong;
	protected Status[] enchantment;
	
	public Artifact(String name, String descr, String descrLong, int statusNum) {
		this.name = name;
		this.descr = descr;
		this.descrLong = descrLong;
		enchantment = new Status[statusNum];
	}
	
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		return null;
	}

	public String getName() { return name; }

	public String getDescr() { return descr; }

	public String getDescrLong() { return descrLong; }

	public Status[] getEnchantment() { return enchantment; }
}
