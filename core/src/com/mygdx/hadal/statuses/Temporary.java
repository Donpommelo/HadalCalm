package com.mygdx.hadal.statuses;

import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;

/**
 * Temporary designates a schmuck as a temporary summon that will disappear at the end of a duration
 * @author Phoposter Purbara
 */
public class Temporary extends Status {

	//this is the lifespan of the temporary unit
	private float duration;
	
	public Temporary(PlayState state, float i, BodyData p, BodyData v, float duration) {
		super(state, i, false, p, v);
		this.duration = duration;
	}
	
	@Override
	public void timePassing(float delta) {
		super.timePassing(delta);
		duration -= delta;
		if (duration <= 0) {
			inflicted.die(inflicted);
		}
	}
	
	public statusStackType getStackType() {
		return statusStackType.REPLACE;
	}
}
