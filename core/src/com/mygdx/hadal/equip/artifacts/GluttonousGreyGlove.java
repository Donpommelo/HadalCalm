package com.mygdx.hadal.equip.artifacts;

import com.badlogic.gdx.math.MathUtils;
import com.mygdx.hadal.equip.WeaponUtils;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;

public class GluttonousGreyGlove extends Artifact {

	private static final int slotCost = 2;
	private static final float heal = 25.0f;
	private static final float chance = 0.2f;
	
	public GluttonousGreyGlove() {
		super(slotCost);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new Status(state, p) {
			
			@Override
			public void onKill(BodyData vic) {
				if (MathUtils.randomBoolean(chance) || vic instanceof PlayerBodyData) {
					WeaponUtils.createPickup(state, vic.getSchmuck().getPixelPosition(), WeaponUtils.pickupTypes.HEALTH, heal);
				}
			}
		};
	}
}
