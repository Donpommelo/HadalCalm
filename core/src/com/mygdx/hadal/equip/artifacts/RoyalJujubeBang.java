package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity.particleSyncType;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.statuses.Status;

public class RoyalJujubeBang extends Artifact {

	private final static int statusNum = 1;
	private final static int slotCost = 2;
	
	private final static float distThreshold = 600.0f;
	private final static float distDamageBoost = 1.5f;
	
	private final static float particleDura = 1.5f;
	
	public RoyalJujubeBang() {
		super(slotCost, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new Status(state, b) {
			
			@Override
			public float onDealDamage(float damage, BodyData vic, DamageTypes... tags) {
				
				float dist = vic.getSchmuck().getPixelPosition().dst(inflicted.getSchmuck().getPixelPosition());
				
				float boost = 1.0f;
				if (dist > distThreshold) {
					boost = distDamageBoost;
					new ParticleEntity(state, vic.getSchmuck(), Particle.EXPLOSION, 1.0f, particleDura, true, particleSyncType.CREATESYNC);
				}

				return damage * boost;
			}
		};
		return enchantment;
	}
}
