package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Lifesteal;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.StatusComposite;

public class RingoftheLamprey extends Artifact {

	private final static String name = "Ring of the Lamprey";
	private final static String descr = "Lifesteal";
	private final static String descrLong = "";
	private final static int statusNum = 1;
	
	public RingoftheLamprey() {
		super(name, descr, descrLong, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new StatusComposite(state, name, descr, b, 
				new Lifesteal(state, 0.03f, b));
		return enchantment;
	}
}
