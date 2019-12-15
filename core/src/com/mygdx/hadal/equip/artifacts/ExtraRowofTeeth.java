package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.StatusComposite;
import com.mygdx.hadal.utils.Stats;

public class ExtraRowofTeeth extends Artifact {

	private final static String name = "Extra Row of Teeth";
	private final static String descr = "+Clip Size";
	private final static String descrLong = "";
	private final static int statusNum = 1;
	
	public ExtraRowofTeeth() {
		super(name, descr, descrLong, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new StatusComposite(state, name, descr, b, 
				new StatChangeStatus(state, Stats.RANGED_CLIP, 0.20f, b));
		return enchantment;
	}
}
