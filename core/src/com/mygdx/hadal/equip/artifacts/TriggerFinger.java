package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.StatusComposite;
import com.mygdx.hadal.utils.Stats;

public class TriggerFinger extends Artifact {

	private final static String name = "Trigger Finger";
	private final static String descr = "+30% Ranged Attack Speed";
	private final static String descrLong = "";
	private final static int statusNum = 1;
	
	public TriggerFinger() {
		super(name, descr, descrLong, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new StatusComposite(state, name, descr, b, 
				new StatChangeStatus(state, Stats.RANGED_ATK_SPD, 0.30f, b)
		);
		return enchantment;
	}
}
