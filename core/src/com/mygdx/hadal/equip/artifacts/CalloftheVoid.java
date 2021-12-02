package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.StatusComposite;
import com.mygdx.hadal.utils.Stats;

public class CalloftheVoid extends Artifact {

	private static final int slotCost = 1;
	
	private static final float damageAmp = 0.3f;
	private static final float damageRes = -0.3f;

	public CalloftheVoid() {
		super(slotCost);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new StatusComposite(state, p,
				new StatChangeStatus(state, Stats.DAMAGE_AMP, damageAmp, p),
				new StatChangeStatus(state, Stats.DAMAGE_RES, damageRes, p));
	}
}
