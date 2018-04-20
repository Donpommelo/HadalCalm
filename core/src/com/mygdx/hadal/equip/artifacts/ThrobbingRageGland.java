package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.artifact.RageGlandStatus;

public class ThrobbingRageGland extends Artifact {

	private final static String name = "Throbbing Rage Gland";
	private final static String descr = "Temporarily boosts speed and damage when taking damage.";
	private final static String descrLong = "";
	private final static int statusNum = 1;
	
	public ThrobbingRageGland() {
		super(name, descr, descrLong, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new RageGlandStatus(state, b, b, 50);
		return enchantment;
	}
}
