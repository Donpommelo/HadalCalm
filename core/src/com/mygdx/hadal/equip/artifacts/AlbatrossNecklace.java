package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.utils.Stats;

public class AlbatrossNecklace extends Artifact {

	private static final int slotCost = 1;
	private static final float bonusHp = 0.75f;
	private static final float gravityScale = 1.6f;
	
	public AlbatrossNecklace() {
		super(slotCost);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new Status(state, p) {
			
			@Override
			public void onInflict() {
				if (p.getPlayer().getBody() != null) {
					p.getSchmuck().setGravityScale(gravityScale);
				} else {
					p.getPlayer().setGravityModifier(gravityScale);
				}
			}
			
			@Override
			public void onRemove() {
				p.getSchmuck().setGravityScale(1.0f);
			}
			
			@Override
			public void statChanges() {
				p.setStat(Stats.MAX_HP_PERCENT, p.getStat(Stats.MAX_HP_PERCENT) + bonusHp);
			}
		};
	}
}
