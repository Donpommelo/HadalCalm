package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.Status;

public class NuclearPunchThrusters extends Artifact {

	private final static String name = "Nuclear Punch-Thrusters";
	private final static String descr = "+50% Knockback";
	private final static String descrLong = "";
	private final static int statusNum = 1;
	
	public NuclearPunchThrusters() {
		super(name, descr, descrLong, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new StatChangeStatus(state, 23, 0.5f, b, b, 50);
		return enchantment;
	}
}
