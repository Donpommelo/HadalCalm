package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.StatusComposite;

public class CastawaysTravelogue extends Artifact {

	private final static int statusNum = 1;
	private final static int slotCost = 2;
	
	private final static float fuelRegen = 20.0f;
	
	private final static float procCd = 2.0f;
	
	public CastawaysTravelogue() {
		super(slotCost, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new StatusComposite(state, b, 
				new Status(state, b) {
			
			private float procCdCount;
			private boolean activated = true;
			private float lastFuelAmount;
			
			@Override
			public void timePassing(float delta) {
				
				//if any fuel has been spent, deactivate effect
				if (inflicted.getCurrentFuel() < lastFuelAmount) {
					activated = false;
					procCdCount = 0.0f;
				}
				
				if (!activated && procCdCount < procCd) {
					procCdCount += delta;
					
					if (procCdCount >= procCd) {
						activated = true;
					}
				}
				
				if (activated) {
					((PlayerBodyData) inflicted).fuelGain(fuelRegen * delta);
				}
				
				lastFuelAmount = inflicted.getCurrentFuel();
			}
		});
		return enchantment;
	}
}
