package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.StatusComposite;
import com.mygdx.hadal.constants.Stats;

public class LotusLantern extends Artifact {

	private static final int slotCost = 1;
	
	private static final float extraScrap = 0.25f;
	private static final float bonusHp = 0.15f;

	public LotusLantern() {
		super(slotCost);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new StatusComposite(state, p,
				new StatChangeStatus(state, Stats.EXTRA_SCRAP, extraScrap, p),
				new StatChangeStatus(state, Stats.MAX_HP_PERCENT, bonusHp, p));
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) (bonusHp * 100)),
				String.valueOf((int) (extraScrap * 100))};
	}
}
