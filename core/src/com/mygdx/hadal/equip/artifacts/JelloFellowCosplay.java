package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.utils.Stats;

public class JelloFellowCosplay extends Artifact {

	private final static int statusNum = 1;
	private final static int slotCost = 1;
	
	private final static float bounce = 1.0f;
	private final static float bonusHp = 25.0f;
	
	public JelloFellowCosplay() {
		super(slotCost, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, final BodyData b) {
		enchantment[0] = new Status(state, b) {
			
			@Override
			public void onInflict() {
				inflicted.getSchmuck().getBody().getFixtureList().get(0).setRestitution(bounce);

			}
			
			@Override
			public void onRemove() {
				inflicted.getSchmuck().getBody().getFixtureList().get(0).setRestitution(0.0f);
			}
			
			@Override
			public void statChanges() {
				inflicted.setStat(Stats.MAX_HP, inflicted.getStat(Stats.MAX_HP) + bonusHp);
			}
		};
		
		return enchantment;
	}
}
