package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.artifact.ScalingScalesStatus;

public class ClawsofFestus extends Artifact {

	private final static String name = "Claws of Festus";
	private final static String descr = "Enables wall clinging.";
	private final static String descrLong = "";
	private final static int statusNum = 1;
	
	public ClawsofFestus() {
		super(name, descr, descrLong, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new ScalingScalesStatus(state, b);
		return enchantment;
	}
}
