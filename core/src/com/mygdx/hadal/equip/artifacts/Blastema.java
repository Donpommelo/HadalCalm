package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity.particleSyncType;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.utils.Stats;

public class Blastema extends Artifact {

	private final static int statusNum = 1;
	private final static int slotCost = 2;
	
	private final static float regenCd = 5.0f;
	private final static float regen = 2.5f;
	
	public Blastema() {
		super(slotCost, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new Status(state, b) {
			
			private float procCdCount;
			private float procCd = regenCd;
			
			@Override
			public void timePassing(float delta) {
				if (procCdCount < procCd) {
					procCdCount += delta;
				}
			}
			
			@Override
			public float onReceiveDamage(float damage, BodyData perp, DamageTypes... tags) {
				
				if (procCdCount >= procCd && damage > 0) {
					procCdCount -= procCd;
					new ParticleEntity(state, inflicted.getSchmuck(), Particle.REGEN, 0.0f, regenCd, true, particleSyncType.TICKSYNC);
					inflicted.addStatus(new StatChangeStatus(state, regenCd, Stats.HP_REGEN, regen, inflicted, inflicted));
				}
				return damage;
			}
		};
		return enchantment;
	}
}
