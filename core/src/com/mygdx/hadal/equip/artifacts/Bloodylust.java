package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.artifact.BloodlustStatus;

public class Bloodylust extends Artifact {

	private final static String name = "Bloody Lust";
	private final static String descr = "75% Reload Speed. Refill 50% clip on kill.";
	private final static String descrLong = "";
	private final static int statusNum = 2;
	
	public Bloodylust() {
		super(name, descr, descrLong, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new BloodlustStatus(state, b, b, 50);
		enchantment[1] = new StatChangeStatus(state, 28, -0.75f, b, b, 50);
		return enchantment;
	}
}
