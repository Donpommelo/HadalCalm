package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.Status;

public class RootBoots extends Artifact {

	private final static String name = "Root-Boots";
	private final static String descr = "+75% Knockback Resistance";
	private final static String descrLong = "";
	private final static int statusNum = 1;
	
	public RootBoots() {
		super(name, descr, descrLong, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new StatChangeStatus(state, 24, 0.75f, b);
		return enchantment;
	}
}
