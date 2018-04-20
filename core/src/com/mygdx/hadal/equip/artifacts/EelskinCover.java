package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.Status;

public class EelskinCover extends Artifact {

	private final static String name = "Eelskin Cover";
	private final static String descr = "Reduces Drag";
	private final static String descrLong = "";
	private final static int statusNum = 2;
	
	public EelskinCover() {
		super(name, descr, descrLong, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new StatChangeStatus(state, 8, -0.60f, b, b, 50);
		enchantment[1] = new StatChangeStatus(state, 9, -0.60f, b, b, 50);
		return enchantment;
	}
}
