package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.StatusComposite;
import com.mygdx.hadal.utils.Stats;

public class CodexofSalvorsLaw extends Artifact {

	private static final int statusNum = 1;
	private static final int slotCost = 1;
	
	private static final float bonusMaxFuel = 20.0f;
	private static final float bonusReflectDamage = 0.5f;
	
	public CodexofSalvorsLaw() {
		super(slotCost, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new StatusComposite(state, b, 
				new StatChangeStatus(state, Stats.MAX_FUEL, bonusMaxFuel, b), 
				new StatChangeStatus(state, Stats.REFLECT_DAMAGE, bonusReflectDamage, b));
		return enchantment;
	}
}
