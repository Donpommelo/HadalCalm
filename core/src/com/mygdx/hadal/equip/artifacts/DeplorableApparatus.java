package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.StatusComposite;
import com.mygdx.hadal.utils.Stats;

public class DeplorableApparatus extends Artifact {

	private final static String name = "Deplorable Apparatus";
	private final static String descr = "-Max Hp. High Hp Regen.";
	private final static String descrLong = "";
	private final static int statusNum = 1;
	
	public DeplorableApparatus() {
		super(name, descr, descrLong, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new StatusComposite(state, name, descr, b, 
				new StatChangeStatus(state, Stats.MAX_HP, -60f, b), 
				new StatChangeStatus(state, Stats.HP_REGEN, 8.0f, b));
		return enchantment;
	}
}
