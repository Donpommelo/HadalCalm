package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.StatusComposite;
import com.mygdx.hadal.utils.Stats;

public class NiceShoes extends Artifact {

	private final static String name = "Nice Shoes";
	private final static String descr = "+30% Ground Speed";
	private final static String descrLong = "";
	private final static int statusNum = 1;
	
	public NiceShoes() {
		super(name, descr, descrLong, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new StatusComposite(state, name, descr, b, 
				new StatChangeStatus(state, Stats.GROUND_SPD, 0.30f, b), 
				new StatChangeStatus(state, Stats.GROUND_ACCEL, 0.50f, b));
		return enchantment;
	}
}
