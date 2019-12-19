package com.mygdx.hadal.equip.artifacts;

import static com.mygdx.hadal.utils.Constants.PPM;

import com.mygdx.hadal.equip.WeaponUtils;
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;

public class GluttonousGreyGlove extends Artifact {

	private final static String name = "Gluttonous Grey Glove";
	private final static String descr = "Occasionally drop a Medpak on Kill.";
	private final static String descrLong = "";
	private final static int statusNum = 1;
	
	private static final float heal = 20.0f;
	private static final float chance = 0.2f;
	
	public GluttonousGreyGlove() {
		super(name, descr, descrLong, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new Status(state, name, descr, b) {
			
			@Override
			public void onKill(BodyData vic) {
				if (GameStateManager.generator.nextFloat() <= chance || vic instanceof PlayerBodyData) {
					WeaponUtils.createPickup(state, WeaponUtils.pickupTypes.HEALTH, heal, (int)(vic.getSchmuck().getPosition().x * PPM), 
							(int)(vic.getSchmuck().getPosition().y * PPM));
				}
			}
		};
		return enchantment;
	}
}
