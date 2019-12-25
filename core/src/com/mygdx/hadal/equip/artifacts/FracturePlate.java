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
	private float cd = 5.0f;
	
	private float particleDura = 1.0f;
	
	public FracturePlate() {
		super(slotCost, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new Status(state, b) {
			
			@Override
			public void timePassing(float delta) {
				procCdCount -= delta;
			}
			
			@Override
			public float onReceiveDamage(float damage, BodyData perp, DamageTypes... tags) {
				if (damage > 0 && procCdCount <= 0) {
					procCdCount = cd;
					damage = 0;
					new ParticleEntity(state, inflicted.getSchmuck(), Particle.SHIELD, 0.0f, particleDura, true, particleSyncType.TICKSYNC);
				}
				return damage;
			}
		};
		return enchantment;
	}
}
