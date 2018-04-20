package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.Status;

public class NiceShoes extends Artifact {

	private final static String name = "Nice Shoes";
	private final static String descr = "+30% Ground Speed";
	private final static String descrLong = "";
	private final static int statusNum = 2;
	
	public NiceShoes() {
		super(name, descr, descrLong, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new StatChangeStatus(state, 4, 0.30f, b, b, 50);
		enchantment[1] = new StatChangeStatus(state, 6, 0.50f, b, b, 50);
		return enchantment;
	}
}
