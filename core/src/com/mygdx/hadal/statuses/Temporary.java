package com.mygdx.hadal.statuses;

import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;

/**
 * Temporary designates a schmuck as a temporary summon that will disappear at the end of a duration
 * @author Zachary Tu
 *
 */
public class Temporary extends Status {

	private static String name = "Temporary";
	private static String descr = "";
	
	private float duration;
	
	public Temporary(PlayState state, float i, BodyData p, BodyData v, float duration) {
		super(state, i, name, descr, false, p, v);
		this.duration = duration;
	}
	
	@Override
	public void timePassing(float delta) {
		super.timePassing(delta);
		duration -= delta;
		if (duration <= 0) {
			inflicted.getSchmuck().queueDeletion();
		}
	}
	
	public statusStackType getStackType() {
		return statusStackType.REPLACE;
	}
}
