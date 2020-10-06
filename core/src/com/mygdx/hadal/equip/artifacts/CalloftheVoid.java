package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.StatusComposite;
import com.mygdx.hadal.utils.Stats;

public class CalloftheVoid extends Artifact {

	private static final int statusNum = 1;
	private static final int slotCost = 1;
	
	private static final float damageAmp = 0.3f;
	private static final float damageRes = -0.3f;

	public CalloftheVoid() {
		super(slotCost, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {		
		enchantment[0] = new StatusComposite(state, b, 
				new StatChangeStatus(state, Stats.DAMAGE_AMP, damageAmp, b), 
				new StatChangeStatus(state, Stats.DAMAGE_RES, damageRes, b));
		return enchantment;
	}
}
