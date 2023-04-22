package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.StatusComposite;
import com.mygdx.hadal.constants.Stats;

public class BloodwoodsGlove extends Artifact {

	private static final int SLOT_COST = 2;
	
	private static final float BONUS_ACTIVE_CHARGE = 0.3f;
	private static final float BONUS_WEAPON_CARGE = 0.3f;
	
	public BloodwoodsGlove() {
		super(SLOT_COST);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new StatusComposite(state, p,
				new StatChangeStatus(state, Stats.ACTIVE_CHARGE_RATE, BONUS_ACTIVE_CHARGE, p),
				new StatChangeStatus(state, Stats.EQUIP_CHARGE_RATE, BONUS_WEAPON_CARGE, p)
		);
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) (BONUS_ACTIVE_CHARGE * 100)),
				String.valueOf((int) (BONUS_WEAPON_CARGE * 100))};
	}
}
