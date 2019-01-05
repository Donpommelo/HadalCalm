package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.StatusComposite;
import com.mygdx.hadal.utils.Stats;

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
		enchantment[0] = new StatusComposite(state, name, descr, b, 
				new StatChangeStatus(state, Stats.KNOCKBACK, 0.6f, b)
		);
		return enchantment;
	}
}
