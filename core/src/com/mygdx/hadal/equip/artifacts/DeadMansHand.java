package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.StatusComposite;
import com.mygdx.hadal.utils.Stats;

public class DeadMansHand extends Artifact {

	private final static String name = "Dead Man's Hand";
	private final static String descr = "+1 Weapon Slot";
	private final static String descrLong = "";
	private final static int statusNum = 1;
	
	public DeadMansHand() {
		super(name, descr, descrLong, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new StatusComposite(state, name, descr, b, 
				new StatChangeStatus(state, Stats.WEAPON_SLOTS, 1.0f, b));
		return enchantment;
	}
}
