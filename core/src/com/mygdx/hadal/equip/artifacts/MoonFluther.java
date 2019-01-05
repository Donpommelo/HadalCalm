package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.StatusComposite;
import com.mygdx.hadal.utils.Stats;

public class MoonFluther extends Artifact {

	private final static String name = "Moon Fluther";
	private final static String descr = "Improved Hovering";
	private final static String descrLong = "";
	private final static int statusNum = 1;
	
	public MoonFluther() {
		super(name, descr, descrLong, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new StatusComposite(state, name, descr, b, 
				new StatChangeStatus(state, Stats.HOVER_POW, 0.25f, b), 
				new StatChangeStatus(state, Stats.HOVER_COST, -0.25f, b));
		return enchantment;
	}
}
