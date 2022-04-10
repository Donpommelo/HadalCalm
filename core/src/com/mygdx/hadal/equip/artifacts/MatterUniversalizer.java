package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.schmucks.SyncType;
import com.mygdx.hadal.schmucks.entities.ParticleEntity;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;

public class MatterUniversalizer extends Artifact {

	private static final int slotCost = 1;
	
	private final float amountEnemy = 25.f;
	private final float amountPlayer = 75.f;
	private final float particleDura = 1.5f;
	
	public MatterUniversalizer() {
		super(slotCost);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new Status(state, p) {

			@Override
			public void onKill(BodyData vic, DamageSource source) {
				SoundEffect.MAGIC2_FUEL.playUniversal(state, p.getSchmuck().getPixelPosition(), 0.4f, false);
				new ParticleEntity(state, p.getSchmuck(), Particle.PICKUP_ENERGY, 1.0f, particleDura, true, SyncType.CREATESYNC);

				if (vic instanceof PlayerBodyData) {
					p.fuelGain(amountPlayer);
				} else {
					p.fuelGain(amountEnemy);
				}
			}
		};
	}
}
