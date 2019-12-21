package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.StatusComposite;
import com.mygdx.hadal.utils.Stats;

public class EightBall extends Artifact {

	private final static String name = "8-Ball";
	private final static String descr = "Large and slow projectiles";
	private final static String descrLong = "";
	private final static int statusNum = 1;
	
	public EightBall() {
		super(name, descr, descrLong, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, final BodyData b) {
		enchantment[0] = new StatusComposite(state, name, descr, b, 
				new StatChangeStatus(state, Stats.RANGED_PROJ_SPD, -0.5f, b),
				new StatChangeStatus(state, Stats.RANGED_PROJ_LIFESPAN, 1.25f, b),
				new StatChangeStatus(state, Stats.RANGED_PROJ_SIZE, 2.0f, b));
		return enchantment;
	}
}
