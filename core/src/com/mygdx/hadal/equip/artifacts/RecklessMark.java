package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.Status;

public class RecklessMark extends Artifact {

	private final static String name = "Reckless Mark";
	private final static String descr = "Deal and take +40% more damage.";
	private final static String descrLong = "";
	private final static int statusNum = 2;
	
	public RecklessMark() {
		super(name, descr, descrLong, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new StatChangeStatus(state, 21, 0.4f, b, b, 50);
		enchantment[1] = new StatChangeStatus(state, 22, -0.4f, b, b, 50);
		return enchantment;
	}
}
