package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.HadalColor;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity.particleSyncType;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.StatusComposite;
import com.mygdx.hadal.utils.Stats;

public class GomezsAmygdala extends Artifact {

	private static final int statusNum = 1;
	private static final int slotCost = 2;
	
	private final float dura = 2.0f;
	private static final float procCd = 2.0f;
	private static final float spdBuff = 0.5f;
	private static final float damageBuff = 0.4f;
	
	public GomezsAmygdala() {
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
					procCdCount -= procCd;
					
					SoundEffect.MAGIC18_BUFF.playUniversal(state, inflicted.getSchmuck().getPixelPosition(), 0.5f, false);

					new ParticleEntity(state, inflicted.getSchmuck(), Particle.PICKUP_ENERGY, 1.0f, procCd, true, particleSyncType.CREATESYNC);
					new ParticleEntity(state, inflicted.getSchmuck(), Particle.BRIGHT, 1.0f, dura, true, particleSyncType.CREATESYNC).setColor(
						HadalColor.RED);
					
					inflicted.addStatus(new StatusComposite(state, dura, false, perp, inflicted,
							new StatChangeStatus(state, Stats.GROUND_SPD, spdBuff, inflicted),
							new StatChangeStatus(state, Stats.DAMAGE_AMP, damageBuff, inflicted)));
				}
				return damage;
			}
		};
		return enchantment;
	}
}
