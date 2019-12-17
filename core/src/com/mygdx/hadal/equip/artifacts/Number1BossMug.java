package com.mygdx.hadal.equip.artifacts;

import static com.mygdx.hadal.utils.Constants.PPM;

import com.mygdx.hadal.equip.WeaponUtils;
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;

public class Number1BossMug extends Artifact {

	private final static String name = "#1 Boss Mug";
	private final static String descr = "Occasionally drop Fuel on Kill.";
	private final static String descrLong = "";
	private final static int statusNum = 1;
	
	private static final int heal = 50;
	private static final float chance = 0.5f;
	
	public Number1BossMug() {
		super(name, descr, descrLong, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new Status(state, name, descr, b) {
			
			@Override
			public void onKill(BodyData vic) {
				if (GameStateManager.generator.nextFloat() <= chance || vic instanceof PlayerBodyData) {
					
					WeaponUtils.createPickup(state, WeaponUtils.pickupTypes.FUEL, heal, (int)(vic.getSchmuck().getPosition().x * PPM), 
							(int)(vic.getSchmuck().getPosition().y * PPM));
				}
			}
		};
		return enchantment;
	}
}
