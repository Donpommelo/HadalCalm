package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.StatusComposite;
import com.mygdx.hadal.constants.Stats;

public class WhiteWhaleCharm extends Artifact {

	private static final int SLOT_COST = 2;
	
	private static final float BONUS_PROJECTILE_SIZE = 0.4f;
	private static final float ATTACK_SPD_REDUCTION = -0.5f;
	private static final float BONUS_DAMAGE = 0.4f;
	
	public WhiteWhaleCharm() {
		super(SLOT_COST);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new StatusComposite(state, p,
				new StatChangeStatus(state, Stats.DAMAGE_AMP, BONUS_DAMAGE, p),
				new StatChangeStatus(state, Stats.RANGED_PROJ_SIZE, BONUS_PROJECTILE_SIZE, p),
				new StatChangeStatus(state, Stats.RANGED_ATK_SPD, ATTACK_SPD_REDUCTION, p)
		);
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) (BONUS_PROJECTILE_SIZE * 100)),
				String.valueOf((int) (BONUS_DAMAGE * 100)),
				String.valueOf((int) -(ATTACK_SPD_REDUCTION * 100))};
	}
}
