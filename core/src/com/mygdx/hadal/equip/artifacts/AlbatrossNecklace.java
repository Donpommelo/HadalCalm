package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.constants.Stats;

public class AlbatrossNecklace extends Artifact {

	private static final int SLOT_COST = 1;
	private static final float BONUS_HP = 0.75f;
	private static final float GRAVITY_SCALE = 0.6f;
	
	public AlbatrossNecklace() {
		super(SLOT_COST);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new Status(state, p) {
			
			@Override
			public void onInflict() {
				if (null != p.getPlayer().getBody()) {
					p.getSchmuck().setGravityScale(1.0f + GRAVITY_SCALE);
				} else {
					p.getPlayer().setGravityModifier(1.0f + GRAVITY_SCALE);
				}
			}
			
			@Override
			public void onRemove() {
				p.getSchmuck().setGravityScale(1.0f);
			}
			
			@Override
			public void statChanges() {
				p.setStat(Stats.MAX_HP_PERCENT, p.getStat(Stats.MAX_HP_PERCENT) + BONUS_HP);
			}
		};
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) (BONUS_HP * 100)),
				String.valueOf((int) (GRAVITY_SCALE * 100))};
	}
}
