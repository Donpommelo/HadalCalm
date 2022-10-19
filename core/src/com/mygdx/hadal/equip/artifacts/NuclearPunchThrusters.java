package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.StatusComposite;
import com.mygdx.hadal.constants.Stats;

public class NuclearPunchThrusters extends Artifact {

	private static final int slotCost = 1;
	
	private static final float bonusKnockback = 0.6f;
	private static final float bonusDamageAmp = 0.1f;

	public NuclearPunchThrusters() {
		super(slotCost);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new StatusComposite(state, p,
			new StatChangeStatus(state, Stats.KNOCKBACK_AMP, bonusKnockback, p),
			new StatChangeStatus(state, Stats.DAMAGE_AMP, bonusDamageAmp, p));
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) (bonusKnockback * 100)),
				String.valueOf((int) (bonusDamageAmp * 100))};
	}
}
