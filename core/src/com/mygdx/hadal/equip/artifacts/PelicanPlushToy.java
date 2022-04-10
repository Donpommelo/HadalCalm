package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.statuses.Status;

public class PelicanPlushToy extends Artifact {

	private static final int slotCost = 1;
	
	private final float amount = 1.5f;
	
	public PelicanPlushToy() {
		super(slotCost);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new Status(state, p) {
			
			@Override
			public float onHeal(float damage, BodyData perp, DamageTag... tags) {
				return damage * amount; 
			}
		};
	}
}
