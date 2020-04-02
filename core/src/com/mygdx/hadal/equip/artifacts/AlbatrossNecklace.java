package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.utils.Stats;

public class AlbatrossNecklace extends Artifact {

	private final static int statusNum = 1;
	private final static int slotCost = 1;
	
	private final static float bonusHp = 60.0f;
	private final static float gravityScale = 2.5f;
	
	public AlbatrossNecklace() {
		super(slotCost, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new Status(state, b) {
			
			@Override
			public void onInflict() {
				inflicted.getSchmuck().setGravityScale(gravityScale);
			}
			
			@Override
			public void onRemove() {
				inflicted.getSchmuck().setGravityScale(1.0f);
			}
			
			@Override
			public void statChanges() {
				inflicted.setStat(Stats.MAX_HP, inflicted.getStat(Stats.MAX_HP) + bonusHp);
			}
		};
		return enchantment;
	}
}
