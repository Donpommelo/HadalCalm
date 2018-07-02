package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.StatusComposite;

public class GoodHealth extends Artifact {

	private final static String name = "Good Health";
	private final static String descr = "+25 Hp";
	private final static String descrLong = "";
	private final static int statusNum = 1;
	
	public GoodHealth() {
		super(name, descr, descrLong, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new StatusComposite(state, name, descr, b, 
				new StatChangeStatus(state, 0, 25, b)
		);
		return enchantment;
	}
}
