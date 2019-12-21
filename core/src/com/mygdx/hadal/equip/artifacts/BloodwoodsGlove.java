package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.StatusComposite;
import com.mygdx.hadal.utils.Stats;

public class BloodwoodsGlove extends Artifact {

	private final static String name = "Bloodwood's Glove";
	private final static String descr = "Lower Charge Time on actives and weapons";
	private final static String descrLong = "";
	private final static int statusNum = 1;
	
	public BloodwoodsGlove() {
		super(name, descr, descrLong, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new StatusComposite(state, name, descr, b,
				new StatChangeStatus(state, Stats.ACTIVE_CHARGE_RATE, 0.30f, b),
				new StatChangeStatus(state, Stats.EQUIP_CHARGE_RATE, 0.30f, b)
		);
		return enchantment;
	}
}
