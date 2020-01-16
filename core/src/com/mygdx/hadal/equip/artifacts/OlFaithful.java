package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.StatusComposite;
import com.mygdx.hadal.utils.Stats;

public class OlFaithful extends Artifact {

	private final static int statusNum = 1;
	private final static int slotCost = 1;
	
	private final static int bonusSlots = -2;
	private static final float bonusAtkSpd = 0.3f;
	private static final float bonusReloadSpd = 0.3f;
	private final static float bonusAmmo = 1.5f;
	
	public OlFaithful() {
		super(slotCost, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new StatusComposite(state, b, 
				new StatChangeStatus(state, Stats.WEAPON_SLOTS, bonusSlots, b), 
				new StatChangeStatus(state, Stats.AMMO_CAPACITY, bonusAmmo, b),
				new StatChangeStatus(state, Stats.RANGED_ATK_SPD, bonusAtkSpd, b),
				new StatChangeStatus(state, Stats.RANGED_RELOAD, bonusReloadSpd, b));
		
		return enchantment;
	}
}