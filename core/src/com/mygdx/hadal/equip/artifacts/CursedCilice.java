package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity.particleSyncType;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.statuses.Status;

public class CursedCilice extends Artifact {

	private static final int slotCost = 1;
	
	private final float amount = 1.5f;
	private static final float procCd = 0.5f;
	private final float particleDura = 1.5f;

	public CursedCilice() {	super(slotCost); }

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
				if (damage > 0) {
					p.fuelGain(damage * amount);
					if (procCdCount >= procCd) {
						procCdCount = 0;
						SoundEffect.MAGIC2_FUEL.playUniversal(state, p.getSchmuck().getPixelPosition(), 0.4f, false);
						new ParticleEntity(state, p.getSchmuck(), Particle.PICKUP_ENERGY, 1.0f, particleDura, true, particleSyncType.CREATESYNC);
					}
				}
				return damage;
			}
		};
	}
}
