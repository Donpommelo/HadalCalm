package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.StatusComposite;
import com.mygdx.hadal.constants.Stats;

public class CodexofSalvorsLaw extends Artifact {

	private static final int slotCost = 1;
	
	private static final float bonusMaxFuel = 20.0f;
	private static final float bonusReflectDamage = 0.75f;
	
	public CodexofSalvorsLaw() {
		super(slotCost);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new StatusComposite(state, p,
				new StatChangeStatus(state, Stats.MAX_FUEL, bonusMaxFuel, p),
				new StatChangeStatus(state, Stats.REFLECT_DAMAGE, bonusReflectDamage, p));
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) bonusMaxFuel),
				String.valueOf((int) (bonusReflectDamage * 100))};
	}
}
