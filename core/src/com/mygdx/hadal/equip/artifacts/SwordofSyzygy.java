package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.StatusComposite;
import com.mygdx.hadal.utils.Stats;

public class SwordofSyzygy extends Artifact {

	private final static String name = "Sword of Syzygy";
	private final static String descr = "+5 Projectile Pierce. +15% Damage";
	private final static String descrLong = "";
	private final static int statusNum = 1;
	
	public SwordofSyzygy() {
		super(name, descr, descrLong, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new StatusComposite(state, name, descr, b, 
				new StatChangeStatus(state, Stats.RANGED_PROJ_DURABILITY, 5.0f, b),
				new StatChangeStatus(state, Stats.DAMAGE_AMP, 0.15f, b));
		return enchantment;
	}
}
