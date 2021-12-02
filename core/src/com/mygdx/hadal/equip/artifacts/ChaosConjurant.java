package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.equip.WeaponUtils;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity.particleSyncType;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.statuses.Status;

public class ChaosConjurant extends Artifact {

	private static final int slotCost = 2;

	private static final float procCd = 5.0f;
	
	private static final float baseDamage = 28.0f;
	private static final float knockback = 6.0f;
	
	private static final float meteorDuration = 1.0f;
	private static final float meteorInterval = 0.1f;
	private static final float spread = 10.0f;
	
	public ChaosConjurant() {
		super(slotCost);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new Status(state, p) {
			
			private float procCdCount = procCd;
			@Override
			public void timePassing(float delta) {
				if (procCdCount < procCd) {
					procCdCount += delta;
				}
			}
			
			@Override
			public float onReceiveDamage(float damage, BodyData perp, Hitbox damaging, DamageTypes... tags) {
				if (procCdCount >= procCd && damage > 0) {
					procCdCount = 0;
					new ParticleEntity(state, p.getSchmuck(), Particle.RING, 1.0f, meteorDuration, true,
							particleSyncType.CREATESYNC).setScale(0.4f);
					WeaponUtils.createMeteors(state, p.getSchmuck().getPosition(), p.getSchmuck(),
							meteorDuration, meteorInterval, spread, baseDamage, knockback);
				}
				return damage;
			}
		};
	}
}
