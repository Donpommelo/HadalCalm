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
				if (dist > 600) {
					boost = 1.75f;
					new ParticleEntity(state, vic.getSchmuck(), Particle.EXPLOSION, 1.5f, 0, true, particleSyncType.CREATESYNC);
				}

				return damage * boost;
			}
		};
		return enchantment;
	}
}
