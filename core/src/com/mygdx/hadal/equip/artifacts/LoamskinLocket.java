package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.constants.Stats;

public class LoamskinLocket extends Artifact {

	private static final int SLOT_COST = 1;

	private static final float BONUS_HP = 0.6f;
	
	public LoamskinLocket() {
		super(SLOT_COST);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new StatChangeStatus(state, Stats.MAX_HP_PERCENT, BONUS_HP, p) {
			
			private float previousHealth;
			private boolean activated;
			@Override
			public void onInflict() { previousHealth = p.getCurrentHp(); }
			
			@Override
			public void onRemove() { p.setCurrentHp(p.getCurrentHp() + BONUS_HP * p.getStat(Stats.MAX_HP)); }
			
			@Override
			public void timePassing(float delta) {
				if (!activated) {
					activated = true;
					p.setCurrentHp(previousHealth);
				}
			}
		};
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) (BONUS_HP * 100))};
	}
}
