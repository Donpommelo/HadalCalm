package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.StatusComposite;

public class BloodwoodsGlove extends Artifact {

	private final static String name = "Bloodwood's Glove";
	private final static String descr = "Slower Reload and Fire Speed. Lower Active Cooldowns";
	private final static String descrLong = "";
	private final static int statusNum = 1;
	
	public BloodwoodsGlove() {
		super(name, descr, descrLong, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new StatusComposite(state, name, descr, b, 
				new StatChangeStatus(state, 27, -0.75f, b),
				new StatChangeStatus(state, 28, -0.5f, b),
				new StatChangeStatus(state, 18, 0.75f, b)
		);
		return enchantment;
	}
}
