package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.StatusComposite;
import com.mygdx.hadal.utils.Stats;

public class ShipinaBottle extends Artifact {

	private static final int slotCost = 1;

	private static final float bonusAmmo = 0.5f;
	private static final float bonusReloadSpd = 0.15f;

	public ShipinaBottle() {
		super(slotCost);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new StatusComposite(state, p,
				new StatChangeStatus(state, Stats.AMMO_CAPACITY, bonusAmmo, p),
				new StatChangeStatus(state, Stats.RANGED_RELOAD, bonusReloadSpd, p));
	}
}
