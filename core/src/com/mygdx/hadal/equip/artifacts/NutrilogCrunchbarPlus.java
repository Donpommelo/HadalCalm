package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.utils.Stats;

import static com.mygdx.hadal.utils.Constants.PRIORITY_LAST_LAST;

public class NutrilogCrunchbarPlus extends Artifact {

	private static final int slotCost = 1;
	private static final float sizeModifier = 0.8f;
	private static final float bonusHp = 1.0f;
	private static final float bonusKnockbackRes = 0.5f;

	public NutrilogCrunchbarPlus() {
		super(slotCost);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {

		//run this status last to override -size modifiers for balance reasons
		enchantment = new Status(state, p) {
			
			@Override
			public void onInflict() {
				if (p.getPlayer().getBody() == null) {
					p.getPlayer().setScaleModifier(sizeModifier);
				}
			}
			
			@Override
			public void statChanges() {
				p.setStat(Stats.MAX_HP_PERCENT, p.getStat(Stats.MAX_HP_PERCENT) + bonusHp);
				p.setStat(Stats.KNOCKBACK_RES, p.getStat(Stats.KNOCKBACK_RES) + bonusKnockbackRes);
			}
		}.setPriority(PRIORITY_LAST_LAST);
	}
}
