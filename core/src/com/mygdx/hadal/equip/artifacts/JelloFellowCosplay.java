package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.constants.Stats;

public class JelloFellowCosplay extends Artifact {

	private static final int SLOT_COST = 1;

	private static final float BOUNCE = 1.0f;
	private static final float BONUS_HP = 0.4f;
	
	public JelloFellowCosplay() {
		super(SLOT_COST);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new Status(state, p) {
			
			@Override
			public void onInflict() {
				if (p.getPlayer().getBody() != null) {
					p.getSchmuck().setRestitution(BOUNCE);
				} else {
					p.getPlayer().setRestitutionModifier(BOUNCE);
				}
			}
			
			@Override
			public void onRemove() {
				p.getSchmuck().setRestitution(0.0f);
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
				String.valueOf((int) (BONUS_HP * 100))};
	}
}
