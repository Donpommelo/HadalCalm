package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.StatusComposite;
import com.mygdx.hadal.utils.Stats;

public class TunicateTunic extends Artifact {

	private final static String name = "Tunicate Tunic";
	private final static String descr = "+75% Knockback Resistance";
	private final static String descrLong = "";
	private final static int statusNum = 1;
	
	public TunicateTunic() {
		super(name, descr, descrLong, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new StatusComposite(state, name, descr, b, 
				new StatChangeStatus(state, Stats.KNOCKBACK_RES, 0.75f, b)
		);
		return enchantment;
	}
}
