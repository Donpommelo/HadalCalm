package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.constants.SyncType;
import com.mygdx.hadal.schmucks.entities.ParticleEntity;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.ClientState;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.statuses.Status;

public class RoyalJujubeBang extends Artifact {

	private static final int SLOT_COST = 2;
	
	private static final float DIST_THRESHOLD = 600.0f;
	private static final float DIST_DAMAGE_BOOST = 0.5f;
	
	private static final float PARTICLE_DURA = 1.5f;
	
	public RoyalJujubeBang() {
		super(SLOT_COST);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new Status(state, p) {
			
			@Override
			public float onDealDamage(float damage, BodyData vic, Hitbox damaging, DamageSource source, DamageTag... tags) {
				
				float distSquared = vic.getSchmuck().getPixelPosition().dst2(p.getSchmuck().getPixelPosition());
				
				float boost = 0.0f;
				if (distSquared > DIST_THRESHOLD * DIST_THRESHOLD) {
					boost = DIST_DAMAGE_BOOST;

					ParticleEntity particle = new ParticleEntity(state, vic.getSchmuck(), Particle.EXPLOSION, 1.0f, PARTICLE_DURA, true,
							SyncType.CREATESYNC);
					if (!state.isServer()) {
						((ClientState) state).addEntity(particle.getEntityID(), particle, false, ClientState.ObjectLayer.HBOX);
					}
				}
				return damage * (1.0f + boost);
			}
		};
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) (DIST_DAMAGE_BOOST * 100))};
	}
}
