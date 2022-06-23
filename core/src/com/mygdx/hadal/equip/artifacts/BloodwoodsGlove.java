package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.StatusComposite;
import com.mygdx.hadal.utils.Stats;

public class BloodwoodsGlove extends Artifact {

	private static final int slotCost = 2;
	
	private static final float bonusActiveCharge = 0.3f;
	private static final float bonusWeaponCarge = 0.3f;
	
	public BloodwoodsGlove() {
		super(slotCost);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new StatusComposite(state, p,
				new StatChangeStatus(state, Stats.ACTIVE_CHARGE_RATE, bonusActiveCharge, p),
				new StatChangeStatus(state, Stats.EQUIP_CHARGE_RATE, bonusWeaponCarge, p)
		);
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) (bonusActiveCharge * 100)),
				String.valueOf((int) (bonusWeaponCarge * 100))};
	}
}
