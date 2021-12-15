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

	private static final int slotCost = 1;
	
	private static final float explosionDamageEnemy = 20.0f;
	private static final float explosionDamagePlayer = 50.0f;
	private static final float explosionKnockback = 18.0f;
	private static final float explosionSize = 200.0f;
	
	private static final float bonusExplosionSize = 0.3f;
	
	public PetrifiedPayload() {
		super(slotCost);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new StatusComposite(state, p,
				new StatChangeStatus(state, Stats.EXPLOSION_SIZE, bonusExplosionSize, p),
				new Status(state, p) {
			
			@Override
			public void onKill(BodyData vic) {
				if (vic instanceof PlayerBodyData) {
					WeaponUtils.createExplosion(state, vic.getSchmuck().getPixelPosition(), explosionSize, p.getSchmuck(),
							explosionDamagePlayer, explosionKnockback, p.getSchmuck().getHitboxfilter(), true);
				} else {
					WeaponUtils.createExplosion(state, vic.getSchmuck().getPixelPosition(), explosionSize, p.getSchmuck(),
							explosionDamageEnemy, explosionKnockback, p.getSchmuck().getHitboxfilter(), true);
				}
			}
			
			@Override
			public void onDeath(BodyData perp) {
				WeaponUtils.createExplosion(state, perp.getSchmuck().getPixelPosition(), explosionSize, p.getSchmuck(),
						explosionDamagePlayer, explosionKnockback, p.getSchmuck().getHitboxfilter(), true);
			}
		});
	}
}
