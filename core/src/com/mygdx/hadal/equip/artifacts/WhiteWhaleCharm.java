package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.StatusComposite;
import com.mygdx.hadal.constants.Stats;

public class WhiteWhaleCharm extends Artifact {

	private static final int slotCost = 2;
	
	private static final float bonusProjectileSize = 0.4f;
	private static final float attackSpdReduction = -0.5f;
	private static final float bonusDamage = 0.4f;
	
	public WhiteWhaleCharm() {
		super(slotCost);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new StatusComposite(state, p,
				new StatChangeStatus(state, Stats.DAMAGE_AMP, bonusDamage, p),
				new StatChangeStatus(state, Stats.RANGED_PROJ_SIZE, bonusProjectileSize, p),
				new StatChangeStatus(state, Stats.RANGED_ATK_SPD, attackSpdReduction, p)
		);
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) (bonusProjectileSize * 100)),
				String.valueOf((int) (bonusDamage * 100)),
				String.valueOf((int) -(attackSpdReduction * 100))};
	}
}
