package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.equip.WeaponUtils;
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;

public class Number1BossMug extends Artifact {

	private static final int statusNum = 1;
	private static final int slotCost = 1;
	
	private static final float ammo = 0.5f;
	private static final float chance = 0.4f;
	
	public Number1BossMug() {
		super(slotCost, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new Status(state, b) {
			
			@Override
			public void onKill(BodyData vic) {
				if (GameStateManager.generator.nextFloat() <= chance || vic instanceof PlayerBodyData) {
					WeaponUtils.createPickup(state, vic.getSchmuck().getPixelPosition(), WeaponUtils.pickupTypes.AMMO, ammo);
				}
			}
		};
		return enchantment;
	}
}
