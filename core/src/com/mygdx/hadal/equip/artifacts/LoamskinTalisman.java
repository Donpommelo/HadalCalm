package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.StatusComposite;

public class LoamskinTalisman extends Artifact {

	private final static String name = "Loamskin Talisman";
	private final static String descr = "+Hp Regen";
	private final static String descrLong = "";
	private final static int statusNum = 1;
	
	public LoamskinTalisman() {
		super(name, descr, descrLong, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new StatusComposite(state, name, descr, b, 
				new StatChangeStatus(state, 2, 1.0f, b)
		);
		return enchantment;
	}
}
