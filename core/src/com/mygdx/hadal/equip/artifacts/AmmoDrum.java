package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.StatusComposite;
import com.mygdx.hadal.utils.Stats;

public class AmmoDrum extends Artifact {

	private final static String name = "Ammo Drum";
	private final static String descr = "+25% Ammo Capacity";
	private final static String descrLong = "";
	private final static int statusNum = 1;
	
	public AmmoDrum() {
		super(name, descr, descrLong, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new StatusComposite(state, name, descr, b, 
				new StatChangeStatus(state, Stats.AMMO_CAPACITY, 0.25f, b));
		return enchantment;
	}
}
