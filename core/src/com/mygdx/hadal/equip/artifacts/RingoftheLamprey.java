package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Lifesteal;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.Status;

public class RingoftheLamprey extends Artifact {

	private final static String name = "Ring of the Lamprey";
	private final static String descr = "3% Lifesteal, -20 Max Hp";
	private final static String descrLong = "";
	private final static int statusNum = 2;
	
	public RingoftheLamprey() {
		super(name, descr, descrLong, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new Lifesteal(state, 0.03f, b, b, 50);
		enchantment[1] = new StatChangeStatus(state, 0, -25, b, b, 50);
		return enchantment;
	}
}
