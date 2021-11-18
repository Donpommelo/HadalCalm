package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.StatusComposite;
import com.mygdx.hadal.utils.Stats;

public class OlFaithful extends Artifact {

	private static final int statusNum = 1;
	private static final int slotCost = 1;
	
	private static final int bonusSlots = -2;
	private static final float bonusDamage = 0.45f;
	private static final float bonusReloadSpd = 0.3f;
	private static final float bonusAmmo = 1.5f;
	
	public OlFaithful() {
		super(slotCost, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new StatusComposite(state, b, 
				new StatChangeStatus(state, Stats.WEAPON_SLOTS, bonusSlots, b), 
				new StatChangeStatus(state, Stats.AMMO_CAPACITY, bonusAmmo, b),
				new StatChangeStatus(state, Stats.DAMAGE_AMP, bonusDamage, b),
				new StatChangeStatus(state, Stats.RANGED_RELOAD, bonusReloadSpd, b));
		return enchantment;
	}
}
