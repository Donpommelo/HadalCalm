package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.equip.WeaponUtils;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.StatusComposite;
import com.mygdx.hadal.utils.Stats;

public class PetrifiedPayload extends Artifact {

	private final static int statusNum = 1;
	private final static int slotCost = 1;
	
	private final static float explosionDamageEnemy = 20.0f;
	private final static float explosionDamagePlayer = 50.0f;
	private final static float explosionKnockback = 18.0f;
	private final static float explosionSize = 200.0f;
	
	private final static float bonusExplosionSize = 0.3f;
	
	public PetrifiedPayload() {
		super(slotCost, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new StatusComposite(state, b, 
				new StatChangeStatus(state, Stats.EXPLOSION_SIZE, bonusExplosionSize, b),
				new Status(state, b) {
			
			@Override
			public void onKill(BodyData vic) {
				if (vic instanceof PlayerBodyData) {
					WeaponUtils.createExplosion(state, vic.getSchmuck().getPixelPosition(), explosionSize, inflicted.getSchmuck(), explosionDamagePlayer, explosionKnockback, inflicted.getSchmuck().getHitboxfilter());
				} else {
					WeaponUtils.createExplosion(state, vic.getSchmuck().getPixelPosition(), explosionSize, inflicted.getSchmuck(), explosionDamageEnemy, explosionKnockback, inflicted.getSchmuck().getHitboxfilter());
				}
			}
			
			@Override
			public void onDeath(BodyData perp) {
				WeaponUtils.createExplosion(state, perp.getSchmuck().getPixelPosition(), explosionSize, inflicted.getSchmuck(), explosionDamagePlayer, explosionKnockback, inflicted.getSchmuck().getHitboxfilter());
			}
		});
		return enchantment;
	}
}
