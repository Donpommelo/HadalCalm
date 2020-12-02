package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.equip.WeaponUtils;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity.particleSyncType;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.statuses.Status;

public class ChaosConjurant extends Artifact {

	private static final int statusNum = 1;
	private static final int slotCost = 2;

	private static final float procCd = 5.0f;
	
	private static final float baseDamage = 24.0f;
	private static final float knockback = 6.0f;
	
	private static final float meteorDuration = 1.0f;
	private static final float meteorInterval = 0.1f;
	private static final float spread = 10.0f;
	
	public ChaosConjurant() {
		super(slotCost, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new Status(state, b) {
			
			private float procCdCount = procCd;
			
			@Override
			public void timePassing(float delta) {
				if (procCdCount < procCd) {
					procCdCount += delta;
				}
			}
			
			@Override
			public float onReceiveDamage(float damage, BodyData perp, DamageTypes... tags) {
				
				if (procCdCount >= procCd && damage > 0) {
					procCdCount = 0;
					new ParticleEntity(state, inflicted.getSchmuck(), Particle.RING, 1.0f, meteorDuration, true, particleSyncType.CREATESYNC).setScale(0.4f);

					WeaponUtils.createMeteors(state, inflicted.getSchmuck().getPosition(), inflicted.getSchmuck(), meteorDuration, meteorInterval, spread, baseDamage, knockback);
				}
				
				return damage;
			}
		};
		return enchantment;
	}
}
