package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.utils.Stats;

public class LoamskinLocket extends Artifact {

	private static final int statusNum = 1;
	private static final int slotCost = 1;
	
	private static final int bonusHp = 60;
	
	public LoamskinLocket() {
		super(slotCost, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new StatChangeStatus(state, Stats.MAX_HP, bonusHp, b) {
			
			private float previousHealth;
			private boolean activated = false;
			
			@Override
			public void onInflict() {
				previousHealth = inflicted.getCurrentHp();
			}
			
			@Override
			public void onRemove() {
				inflicted.setCurrentHp(inflicted.getCurrentHp() + bonusHp);
			}
			
			@Override
			public void timePassing(float delta) {
				if (!activated) {
					activated = true;
					inflicted.setCurrentHp(previousHealth);
				}
			}
		};
		return enchantment;
	}
}
