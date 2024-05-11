package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.constants.Stats;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.StatusComposite;

public class SaltSkullMarmalade extends Artifact {

	private static final int SLOT_COST = 2;

	private static final float BONUS_CAMERA_SHAKE = 1.1f;
	private static final float HP_REGEN = 6.0f;

	public SaltSkullMarmalade() {
		super(SLOT_COST);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new StatusComposite(state, p,
				new StatChangeStatus(state, Stats.CAMERA_SHAKE, BONUS_CAMERA_SHAKE, p),
				new StatChangeStatus(state, Stats.HP_REGEN, HP_REGEN, p));
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) (BONUS_CAMERA_SHAKE * 100)),
				String.valueOf((int) HP_REGEN)};
	}
}
