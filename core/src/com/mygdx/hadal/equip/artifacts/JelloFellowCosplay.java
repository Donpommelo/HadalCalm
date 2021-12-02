package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.utils.Stats;

public class JelloFellowCosplay extends Artifact {

	private static final int slotCost = 1;
	private static final float bounce = 1.0f;
	private static final float bonusHp = 0.4f;
	
	public JelloFellowCosplay() {
		super(slotCost);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new Status(state, p) {
			
			@Override
			public void onInflict() { p.getSchmuck().setRestitution(bounce);	}
			
			@Override
			public void onRemove() {
				p.getSchmuck().setRestitution(0.0f);
			}
			
			@Override
			public void statChanges() {
				p.setStat(Stats.MAX_HP_PERCENT, p.getStat(Stats.MAX_HP_PERCENT) + bonusHp);
			}
		};
	}
}
