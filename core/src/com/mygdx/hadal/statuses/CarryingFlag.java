package com.mygdx.hadal.statuses;

import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.Stats;

/**
 * This status is inflicted upon units that pick up the flag. It is a debuff intended to make capturing flags harder
 * @author Luggozzerella Lopants
 */
public class CarryingFlag extends Status {

	//this is the magnitude of the slow.
	private static final float fuelRegen = 4.0f;

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
