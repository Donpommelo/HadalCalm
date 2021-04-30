package com.mygdx.hadal.statuses;

import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.Stats;

/**
 */
public class CarryingFlag extends Status {

	//this is the magnitude of the slow.
	private static final float fuelRegen = 0.0f;


	public CarryingFlag(PlayState state, BodyData i) {
		super(state, i);
	}

	@Override
	public void statChanges() {
		inflicted.setStat(Stats.FUEL_REGEN, fuelRegen);
	}
	
	public statusStackType getStackType() {
		return statusStackType.REPLACE;
	}
}
