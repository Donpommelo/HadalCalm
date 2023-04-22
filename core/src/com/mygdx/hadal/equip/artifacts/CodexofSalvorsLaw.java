package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.StatusComposite;
import com.mygdx.hadal.constants.Stats;

public class CodexofSalvorsLaw extends Artifact {

	private static final int SLOT_COST = 1;
	
	private static final float BONUS_MAX_FUEL = 20.0f;
	private static final float BONUS_REFLECT_DAMAGE = 0.75f;
	
	public CodexofSalvorsLaw() {
		super(SLOT_COST);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new StatusComposite(state, p,
				new StatChangeStatus(state, Stats.MAX_FUEL, BONUS_MAX_FUEL, p),
				new StatChangeStatus(state, Stats.REFLECT_DAMAGE, BONUS_REFLECT_DAMAGE, p));
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) BONUS_MAX_FUEL),
				String.valueOf((int) (BONUS_REFLECT_DAMAGE * 100))};
	}
}
