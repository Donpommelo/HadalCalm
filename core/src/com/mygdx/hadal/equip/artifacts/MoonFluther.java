package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.Status;

public class MoonFluther extends Artifact {

	private final static String name = "Moon Fluther";
	private final static String descr = "+25% Hovering Power and -25% Hovering Cost";
	private final static String descrLong = "";
	private final static int statusNum = 2;
	
	public MoonFluther() {
		super(name, descr, descrLong, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new StatChangeStatus(state, 12, 0.25f, b, b, 50);
		enchantment[1] = new StatChangeStatus(state, 13, -0.25f, b, b, 50);
		return enchantment;
	}
}
