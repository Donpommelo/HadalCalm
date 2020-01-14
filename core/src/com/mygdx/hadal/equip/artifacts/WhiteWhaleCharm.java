package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.StatusComposite;
import com.mygdx.hadal.utils.Stats;

public class WhiteWhaleCharm extends Artifact {

	private final static int statusNum = 1;
	private final static int slotCost = 2;
	
	private final static float bonusProjectileSize = 0.4f;
	private final static float attackSpdReduction = -0.5f;
	private final static float bonusDamage = 0.4f;
	
	public WhiteWhaleCharm() {
		super(slotCost, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new StatusComposite(state, b,
				new StatChangeStatus(state, Stats.DAMAGE_AMP, bonusDamage, b),
				new StatChangeStatus(state, Stats.RANGED_PROJ_SIZE, bonusProjectileSize, b),
				new StatChangeStatus(state, Stats.RANGED_ATK_SPD, attackSpdReduction, b)
		);
		return enchantment;
	}
}
