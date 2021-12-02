package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity.particleSyncType;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.utils.Stats;

public class FracturePlate extends Artifact {

	private static final int slotCost = 2;
	
	private float procCdCount = 0;
	private static final float cd = 4.0f;
	
	private static final float particleDura = 1.0f;
	private static final float maxShield = 0.2f;
	private float shield;
	
	public FracturePlate() {
		super(slotCost);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new Status(state, p) {
			
			@Override
			public void timePassing(float delta) {
				if (procCdCount >= 0) {
					procCdCount -= delta;
				}
				
				if (procCdCount < 0 && shield != maxShield * p.getStat(Stats.MAX_HP)) {
					shield = maxShield * p.getStat(Stats.MAX_HP);
					new ParticleEntity(state, p.getSchmuck(), Particle.SHIELD, 1.0f, particleDura, true, particleSyncType.CREATESYNC);
				}
			}
			
			@Override
			public float onReceiveDamage(float damage, BodyData perp, Hitbox damaging, DamageTypes... tags) {
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
					new ParticleEntity(state, p.getSchmuck(), Particle.BOULDER_BREAK, 0.0f, particleDura, true, particleSyncType.CREATESYNC);
				}
				return finalDamage;
			}
		};
	}
}
