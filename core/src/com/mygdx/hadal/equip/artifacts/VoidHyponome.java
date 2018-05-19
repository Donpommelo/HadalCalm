package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.Status;

public class VoidHyponome extends Artifact {

	private final static String name = "Void Hyponome";
	private final static String descr = "-25% Airblast Cost";
	private final static String descrLong = "";
	private final static int statusNum = 1;
	
	public VoidHyponome() {
		super(name, descr, descrLong, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new StatChangeStatus(state, 15, -0.25f, b);
		return enchantment;
	}
}
