package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;

public class NothingArtifact extends Artifact {

	private final static int statusNum = 0;
	private final static int slotCost = 0;
	
	public NothingArtifact() {
		super(slotCost, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		return enchantment;
	}
}
