package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.StatusComposite;
import com.mygdx.hadal.constants.Stats;

public class ShipinaBottle extends Artifact {

	private static final int SLOT_COST = 1;

	private static final float BONUS_AMMO = 0.5f;
	private static final float BONUS_RELOAD_SPD = 0.15f;

	public ShipinaBottle() {
		super(SLOT_COST);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new StatusComposite(state, p,
				new StatChangeStatus(state, Stats.AMMO_CAPACITY, BONUS_AMMO, p),
				new StatChangeStatus(state, Stats.RANGED_RELOAD, BONUS_RELOAD_SPD, p));
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) (BONUS_AMMO * 100)),
				String.valueOf((int) (BONUS_RELOAD_SPD * 100))};
	}
}
