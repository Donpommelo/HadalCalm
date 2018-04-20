package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.artifact.ConfidenceStatus;

public class Confidence extends Artifact {

	private final static String name = "Confidence";
	private final static String descr = "+50% damage at Max Hp.";
	private final static String descrLong = "";
	private final static int statusNum = 1;
	
	public Confidence() {
		super(name, descr, descrLong, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new ConfidenceStatus(state, b, b, 50);
		return enchantment;
	}
}
