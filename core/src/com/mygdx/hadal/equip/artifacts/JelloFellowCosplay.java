package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.utils.Stats;

public class JelloFellowCosplay extends Artifact {

	private static final int statusNum = 1;
	private static final int slotCost = 1;
	
	private static final float bounce = 1.0f;
	private static final float bonusHp = 0.4f;
	
	public JelloFellowCosplay() {
		super(slotCost, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, final BodyData b) {
		enchantment[0] = new Status(state, b) {
			
			@Override
			public void onInflict() {
				inflicted.getSchmuck().setRestitution(bounce);
			}
			
			@Override
			public void onRemove() {
				inflicted.getSchmuck().setRestitution(0.0f);
			}
			
			@Override
			public void statChanges() {
				inflicted.setStat(Stats.MAX_HP_PERCENT, inflicted.getStat(Stats.MAX_HP_PERCENT) + bonusHp);
			}
		};
		
		return enchantment;
	}
}
