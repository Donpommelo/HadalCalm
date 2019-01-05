package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.StatusComposite;
import com.mygdx.hadal.utils.Stats;

public class TempestTeapot extends Artifact {

	private final static String name = "Tempest Teapot";
	private final static String descr = "+50% Airblast Knockback and Size.";
	private final static String descrLong = "";
	private final static int statusNum = 1;
	
	public TempestTeapot() {
		super(name, descr, descrLong, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new StatusComposite(state, name, descr, b, 
				new StatChangeStatus(state, Stats.BOOST_POW, 0.5f, b),
				new StatChangeStatus(state, Stats.BOOST_SIZE, 0.5f, b)
		);
		return enchantment;
	}
}
