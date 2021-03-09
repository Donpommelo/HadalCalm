package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity.particleSyncType;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.statuses.Status;

public class RoyalJujubeBang extends Artifact {

	private static final int statusNum = 1;
	private static final int slotCost = 2;
	
	private static final float distThreshold = 600.0f;
	private static final float distDamageBoost = 1.5f;
	
	private static final float particleDura = 1.5f;
	
	public RoyalJujubeBang() {
		super(slotCost, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new Status(state, b) {
			
			@Override
			public float onDealDamage(float damage, BodyData vic, DamageTypes... tags) {
				
				float distSquared = vic.getSchmuck().getPixelPosition().dst2(inflicted.getSchmuck().getPixelPosition());
				
				float boost = 1.0f;
				if (distSquared > distThreshold * distThreshold) {
					boost = distDamageBoost;
					new ParticleEntity(state, vic.getSchmuck(), Particle.EXPLOSION, 1.0f, particleDura, true, particleSyncType.CREATESYNC);
				}

				return damage * boost;
			}
		};
		return enchantment;
	}
}
