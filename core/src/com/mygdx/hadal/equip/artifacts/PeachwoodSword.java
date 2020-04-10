package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.equip.WeaponUtils;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;

public class PeachwoodSword extends Artifact {

	private final static int statusNum = 1;
	private final static int slotCost = 1;
	
	private final static float spiritLifespan = 6.0f;
	private final static float spiritDamageEnemy = 15.0f;
	private final static float spiritDamagePlayer = 50.0f;
	private final static float spiritKnockback = 8.0f;
	
	public PeachwoodSword() {
		super(slotCost, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new Status(state, b) {
			
			@Override
			public void onKill(BodyData vic) {
				SoundEffect.DARKNESS2.playUniversal(state, vic.getSchmuck().getPixelPosition(), 0.2f, false);

				if (vic instanceof PlayerBodyData) {
					WeaponUtils.releaseVengefulSpirits(state, vic.getSchmuck().getPixelPosition(), spiritLifespan, spiritDamagePlayer, spiritKnockback, inflicted, inflicted.getSchmuck().getHitboxfilter());
				} else {
					WeaponUtils.releaseVengefulSpirits(state, vic.getSchmuck().getPixelPosition(), spiritLifespan, spiritDamageEnemy, spiritKnockback, inflicted, inflicted.getSchmuck().getHitboxfilter());
				}
				
			}
			
			@Override
			public void onDeath(BodyData perp) {
				SoundEffect.DARKNESS2.playUniversal(state, inflicted.getSchmuck().getPixelPosition(), 0.2f, false);

				WeaponUtils.releaseVengefulSpirits(state, inflicted.getSchmuck().getPixelPosition(), spiritLifespan, spiritDamagePlayer, spiritKnockback, inflicted, inflicted.getSchmuck().getHitboxfilter());
			}
		};
		return enchantment;
	}
}
