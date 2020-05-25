package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity.particleSyncType;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;

public class MatterUniversalizer extends Artifact {

	private final static int statusNum = 1;
	private final static int slotCost = 1;
	
	private final float amountEnemy = 20.f;
	private final float amountPlayer = 60.f;
	private final float particleDura = 1.5f;
	
	public MatterUniversalizer() {
		super(slotCost, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new Status(state, b) {

			@Override
			public void onKill(BodyData vic) {
				SoundEffect.MAGIC2_FUEL.playUniversal(state, inflicted.getSchmuck().getPixelPosition(), 0.4f, false);

				new ParticleEntity(state, inflicted.getSchmuck(), Particle.PICKUP_ENERGY, 1.0f, particleDura, true, particleSyncType.CREATESYNC);
				
				if (vic instanceof PlayerBodyData) {
					((PlayerBodyData) inflicted).fuelGain(amountPlayer);

				} else {
					((PlayerBodyData) inflicted).fuelGain(amountEnemy);
				}
			}
		};
		return enchantment;
	}
}
