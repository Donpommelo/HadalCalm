package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.StatusComposite;

public class UnbreathingMembrane extends Artifact {

	private final static String name = "Unbreathing Membrane";
	private final static String descr = "Disables Walking. +Recoil and Clipsize";
	private final static String descrLong = "";
	private final static int statusNum = 1;
	
	public UnbreathingMembrane() {
		super(name, descr, descrLong, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new StatusComposite(state, name, descr, b, 
				new StatChangeStatus(state, 4, -0.50f, b), 
				new StatChangeStatus(state, 6, -0.50f, b),
				new StatChangeStatus(state, 29, 1.0f, b),
				new StatChangeStatus(state, 36, 5.0f, b)
				);
		return enchantment;
	}
}
