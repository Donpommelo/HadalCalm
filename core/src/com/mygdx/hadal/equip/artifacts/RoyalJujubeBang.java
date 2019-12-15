package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity.particleSyncType;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.statuses.Status;

public class RoyalJujubeBang extends Artifact {

	private final static String name = "Royal Jujube Bang";
	private final static String descr = "Deal bonus damage from a distance.";
	private final static String descrLong = "";
	private final static int statusNum = 1;
	
	public RoyalJujubeBang() {
		super(name, descr, descrLong, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new Status(state, name, descr, b) {
			
			@Override
			public float onDealDamage(float damage, BodyData vic, DamageTypes... tags) {
				
				float dist = vic.getSchmuck().getPosition().dst(inflicted.getSchmuck().getPosition());
				
				float boost = 1.0f;
				
				if (dist > 6) {
					boost = 1.2f;
				}
				if (dist > 12) {
					boost = 1.75f;
					new ParticleEntity(state, vic.getSchmuck(), Particle.EXPLOSION, 1.5f, 0, true, particleSyncType.CREATESYNC);
				}

				return damage * boost;
			}
		};
		return enchantment;
	}
}
