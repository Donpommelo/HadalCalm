package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.StatusComposite;

public class SwordofSyzygy extends Artifact {

	private final static String name = "Sword of Syzygy";
	private final static String descr = "+3 Projectile Pierce";
	private final static String descrLong = "";
	private final static int statusNum = 1;
	
	public SwordofSyzygy() {
		super(name, descr, descrLong, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new StatusComposite(state, name, descr, b, 
				new StatChangeStatus(state, 34, 3.0f, b));
		return enchantment;
	}
}
