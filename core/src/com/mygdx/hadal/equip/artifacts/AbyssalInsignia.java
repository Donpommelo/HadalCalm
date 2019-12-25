package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.equip.WeaponUtils;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;

public class AbyssalInsignia extends Artifact {

	private final static int statusNum = 1;
	private final static int slotCost = 2;
	
	private final static float spiritLifespan = 6.0f;
	private final static float spiritDamage = 25.0f;
	private final static float spiritKnockback = 8.0f;
	
	public AbyssalInsignia() {
		super(slotCost, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new Status(state, b) {
			
			@Override
			public void onKill(BodyData vic) {
				WeaponUtils.releaseVengefulSpirits(state, vic.getSchmuck().getPixelPosition(), spiritLifespan, spiritDamage, spiritKnockback, inflicted, inflicted.getSchmuck().getHitboxfilter());
			}
			
			@Override
			public void onDeath(BodyData perp) {
				WeaponUtils.releaseVengefulSpirits(state, inflicted.getSchmuck().getPixelPosition(), spiritLifespan, spiritDamage, spiritKnockback, inflicted, inflicted.getSchmuck().getHitboxfilter());
			}
		};
		return enchantment;
	}
}
