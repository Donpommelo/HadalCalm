package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.StatusComposite;
import com.mygdx.hadal.constants.Stats;

public class VowofEmptyHands extends Artifact {

	private static final int SLOT_COST = 1;
	
	private static final int BONUS_SLOTS = -2;
	private static final float BONUS_DAMAGE = 0.25f;
	private static final float BONUS_RELOAD_SPD = 0.3f;
	private static final float BONUS_CLIP_SIZE = 0.15f;
	
	public VowofEmptyHands() {
		super(SLOT_COST);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new StatusComposite(state, p,
				new StatChangeStatus(state, Stats.WEAPON_SLOTS, BONUS_SLOTS, p),
				new StatChangeStatus(state, Stats.RANGED_CLIP, BONUS_CLIP_SIZE, p),
				new StatChangeStatus(state, Stats.DAMAGE_AMP, BONUS_DAMAGE, p),
				new StatChangeStatus(state, Stats.RANGED_RELOAD, BONUS_RELOAD_SPD, p));
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) (BONUS_CLIP_SIZE * 100)),
				String.valueOf((int) (BONUS_RELOAD_SPD * 100)),
				String.valueOf((int) (BONUS_DAMAGE * 100))};
	}
}
