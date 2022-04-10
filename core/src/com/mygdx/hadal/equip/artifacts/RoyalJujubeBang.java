package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.schmucks.SyncType;
import com.mygdx.hadal.schmucks.entities.ParticleEntity;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.statuses.Status;

public class RoyalJujubeBang extends Artifact {

	private static final int slotCost = 2;
	
	private static final float distThreshold = 600.0f;
	private static final float distDamageBoost = 1.5f;
	
	private static final float particleDura = 1.5f;
	
	public RoyalJujubeBang() {
		super(slotCost);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new Status(state, p) {
			
			@Override
			public float onDealDamage(float damage, BodyData vic, Hitbox damaging, DamageSource source, DamageTag... tags) {
				
				float distSquared = vic.getSchmuck().getPixelPosition().dst2(p.getSchmuck().getPixelPosition());
				
				float boost = 1.0f;
				if (distSquared > distThreshold * distThreshold) {
					boost = distDamageBoost;
					new ParticleEntity(state, vic.getSchmuck(), Particle.EXPLOSION, 1.0f, particleDura, true, SyncType.CREATESYNC);
				}
				return damage * boost;
			}
		};
	}
}
