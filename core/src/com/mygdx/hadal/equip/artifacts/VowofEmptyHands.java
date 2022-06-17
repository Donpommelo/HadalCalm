package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.StatusComposite;
import com.mygdx.hadal.utils.Stats;

public class VowofEmptyHands extends Artifact {

	private static final int slotCost = 1;
	
	private static final int bonusSlots = -2;
	private static final float bonusDamage = 0.25f;
	private static final float bonusReloadSpd = 0.3f;
	private static final float bonusClipSize = 0.15f;
	
	public VowofEmptyHands() {
		super(slotCost);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new StatusComposite(state, p,
				new StatChangeStatus(state, Stats.WEAPON_SLOTS, bonusSlots, p),
				new StatChangeStatus(state, Stats.RANGED_CLIP, bonusClipSize, p),
				new StatChangeStatus(state, Stats.DAMAGE_AMP, bonusDamage, p),
				new StatChangeStatus(state, Stats.RANGED_RELOAD, bonusReloadSpd, p));
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) (bonusClipSize * 100)),
				String.valueOf((int) (bonusReloadSpd * 100)),
				String.valueOf((int) (bonusDamage * 100))};
	}
}
