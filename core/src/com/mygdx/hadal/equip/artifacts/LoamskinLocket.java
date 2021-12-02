package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.utils.Stats;

public class LoamskinLocket extends Artifact {

	private static final int slotCost = 1;
	private static final float bonusHp = 0.6f;
	
	public LoamskinLocket() {
		super(slotCost);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new StatChangeStatus(state, Stats.MAX_HP_PERCENT, bonusHp, p) {
			
			private float previousHealth;
			private boolean activated;
			@Override
			public void onInflict() {
				previousHealth = p.getCurrentHp();
			}
			
			@Override
			public void onRemove() {
				p.setCurrentHp(p.getCurrentHp() + bonusHp);
			}
			
			@Override
			public void timePassing(float delta) {
				if (!activated) {
					activated = true;
					p.setCurrentHp(previousHealth);
				}
			}
		};
	}
}
