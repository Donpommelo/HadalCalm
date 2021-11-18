package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.StatusComposite;
import com.mygdx.hadal.utils.Stats;

public class CastawaysTravelogue extends Artifact {

	private static final int statusNum = 1;
	private static final int slotCost = 2;

	private static final float fuelRegen = 30.0f;
	private static final float fuelDuration = 1.0f;
	private static final float fuelThreshold = 4.0f;

	private static final float procCd = 7.5f;
	
	public CastawaysTravelogue() {
		super(slotCost, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new StatusComposite(state, b, 
				new Status(state, b) {
			
			private float procCdCount = procCd;
			@Override
			public void timePassing(float delta) {
				
				if (procCdCount < procCd) {
					procCdCount += delta;
				}

				if (procCdCount >= procCd) {
					if (inflicted.getCurrentFuel() <= fuelThreshold) {
						SoundEffect.MAGIC2_FUEL.playUniversal(state, inflicted.getSchmuck().getPixelPosition(), 0.4f, false);
						inflicted.addStatus(
								new StatChangeStatus(state, fuelDuration, Stats.FUEL_REGEN, fuelRegen, inflicted, inflicted));

						procCdCount = 0.0f;
					}
				}
			}
		});
		return enchantment;
	}
}
