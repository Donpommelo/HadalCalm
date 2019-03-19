package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;

public class NothingArtifact extends Artifact {

	private final static String name = "Nothing";
	private final static String descr = "Does Nothing";
	private final static String descrLong = "";
	private final static int statusNum = 0;
	
	public NothingArtifact() {
		super(name, descr, descrLong, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		return enchantment;
	}
}
