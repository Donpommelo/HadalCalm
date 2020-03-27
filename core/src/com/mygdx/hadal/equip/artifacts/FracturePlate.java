package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity.particleSyncType;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.statuses.Status;

public class FracturePlate extends Artifact {

	private final static int statusNum = 1;
	private final static int slotCost = 2;
	
	private float procCdCount = 0;
	private final static float cd = 8.0f;
	
	private final static float particleDura = 1.0f;
	
	private final static float maxShield = 15.0f;

	private float shield;
	
	public FracturePlate() {
		super(slotCost, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new Status(state, b) {
			
			@Override
			public void timePassing(float delta) {
				
				if (procCdCount >= 0) {
					procCdCount -= delta;
				}
				
				if (procCdCount < 0 && shield != maxShield) {
					shield = maxShield;
					new ParticleEntity(state, inflicted.getSchmuck(), Particle.SHIELD, 0.0f, particleDura, true, particleSyncType.CREATESYNC);
				}
			}
			
			@Override
			public float onReceiveDamage(float damage, BodyData perp, DamageTypes... tags) {
				
				float finalDamage = damage;
				if (damage > 0 && shield > 0) {
					procCdCount = cd;
					if (shield > damage) {
						shield -= damage;
						finalDamage = 0;
					} else {
						finalDamage = damage - shield;
						shield = 0;
					}
					new ParticleEntity(state, inflicted.getSchmuck(), Particle.BOULDER_BREAK, 0.0f, particleDura, true, particleSyncType.CREATESYNC);
				}
				return finalDamage;
			}
		};
		return enchantment;
	}
}
