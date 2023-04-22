package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.StatusComposite;
import com.mygdx.hadal.constants.Stats;

public class CalloftheVoid extends Artifact {

	private static final int SLOT_COST = 1;
	
	private static final float DAMAGE_AMP = 0.3f;
	private static final float DAMAGE_RES = -0.3f;

	public CalloftheVoid() {
		super(SLOT_COST);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new StatusComposite(state, p,
				new StatChangeStatus(state, Stats.DAMAGE_AMP, DAMAGE_AMP, p),
				new StatChangeStatus(state, Stats.DAMAGE_RES, DAMAGE_RES, p));
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) (DAMAGE_AMP * 100)),
				String.valueOf((int) -(DAMAGE_RES * 100))};
	}
}
