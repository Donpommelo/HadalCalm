package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.StatusComposite;
import com.mygdx.hadal.utils.Stats;

public class EelskinCover extends Artifact {

	private final static String name = "Eelskin Cover";
	private final static String descr = "Reduces Drag";
	private final static String descrLong = "";
	private final static int statusNum = 1;
	
	public EelskinCover() {
		super(name, descr, descrLong, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new StatusComposite(state, name, descr, b, 
				new StatChangeStatus(state, Stats.AIR_DRAG, -0.60f, b), 
				new StatChangeStatus(state, Stats.GROUND_DRAG, -0.60f, b));
		return enchantment;
	}
}
