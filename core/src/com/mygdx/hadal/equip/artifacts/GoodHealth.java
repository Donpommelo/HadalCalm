package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.constants.Stats;

public class GoodHealth extends Artifact {

	private static final int slotCost = 2;
	private static final float bonusHp = 0.4f;
	
	public GoodHealth() {
		super(slotCost);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new StatChangeStatus(state, Stats.MAX_HP_PERCENT, bonusHp, p);
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) (bonusHp * 100))};
	}
}
