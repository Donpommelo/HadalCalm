package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.StatusComposite;

public class SkateWings extends Artifact {

	private final static String name = "Skate Wings";
	private final static String descr = "+1 Jump, +20% Jump Power";
	private final static String descrLong = "";
	private final static int statusNum = 1;
	
	public SkateWings() {
		super(name, descr, descrLong, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new StatusComposite(state, name, descr, b, 
				new StatChangeStatus(state, 10, 0.2f, b), 
				new StatChangeStatus(state, 11, 1, b));
		return enchantment;
	}
}
