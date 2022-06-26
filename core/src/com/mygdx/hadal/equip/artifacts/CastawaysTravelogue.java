package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.schmucks.SyncType;
import com.mygdx.hadal.schmucks.entities.ParticleEntity;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.StatusComposite;
import com.mygdx.hadal.utils.Stats;

public class CastawaysTravelogue extends Artifact {

	private static final int slotCost = 2;

	private static final float fuelRegen = 30.0f;
	private static final float fuelDuration = 1.0f;
	private static final float fuelThreshold = 5.0f;

	private static final float procCd = 7.5f;

	private final float particleDura = 1.5f;

	public CastawaysTravelogue() {
		super(slotCost);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new StatusComposite(state, p,
				new Status(state, p) {
			
			private float procCdCount = procCd;
			@Override
			public void timePassing(float delta) {
				if (procCdCount < procCd) {
					procCdCount += delta;
				}

				if (procCdCount >= procCd) {
					if (inflicted.getCurrentFuel() <= fuelThreshold) {
						SoundEffect.MAGIC2_FUEL.playUniversal(state, p.getSchmuck().getPixelPosition(), 0.4f, false);
						new ParticleEntity(state, p.getSchmuck(), Particle.PICKUP_ENERGY, 1.0f, particleDura, true, SyncType.CREATESYNC);

						p.addStatus(new StatChangeStatus(state, fuelDuration, Stats.FUEL_REGEN, fuelRegen, p, p));
						procCdCount = 0.0f;
					}
				}
			}
		});
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf(procCd),
				String.valueOf((int) fuelRegen)};
	}
}
