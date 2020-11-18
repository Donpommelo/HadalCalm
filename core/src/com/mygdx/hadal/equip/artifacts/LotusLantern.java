package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.StatusComposite;
import com.mygdx.hadal.utils.Stats;

public class LotusLantern extends Artifact {

	private static final int statusNum = 1;
	private static final int slotCost = 1;
	
	private static final float extraScrap = 0.25f;
	private static final int bonusHp = 15;

	public LotusLantern() {
		super(slotCost, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new StatusComposite(state, b, 
				new StatChangeStatus(state, Stats.EXTRA_SCRAP, extraScrap, b),
				new StatChangeStatus(state, Stats.MAX_HP, bonusHp, b));
		return enchantment;
	}
}
