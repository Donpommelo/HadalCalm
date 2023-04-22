package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.constants.Stats;

import static com.mygdx.hadal.constants.Constants.PRIORITY_LAST_LAST;

public class NutrilogCrunchbarPlus extends Artifact {

	private static final int SLOT_COST = 1;

	private static final float SIZE_MODIFIER = 0.8f;
	private static final float BONUS_HP = 1.0f;
	private static final float BONUS_KNOCKBACK_RES = 0.5f;

	public NutrilogCrunchbarPlus() {
		super(SLOT_COST);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {

		//run this status last to override -size modifiers for balance reasons
		enchantment = new Status(state, p) {
			
			@Override
			public void onInflict() {
				if (p.getPlayer().getBody() == null) {
					p.getPlayer().setScaleModifier(SIZE_MODIFIER);
				}
			}
			
			@Override
			public void statChanges() {
				p.setStat(Stats.MAX_HP_PERCENT, p.getStat(Stats.MAX_HP_PERCENT) + BONUS_HP);
				p.setStat(Stats.KNOCKBACK_RES, p.getStat(Stats.KNOCKBACK_RES) + BONUS_KNOCKBACK_RES);
			}
		}.setPriority(PRIORITY_LAST_LAST);
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) (BONUS_HP * 100)),
				String.valueOf((int) (BONUS_KNOCKBACK_RES * 100)),
				String.valueOf((int) (SIZE_MODIFIER * 100))};
	}
}
