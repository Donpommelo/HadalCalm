package com.mygdx.hadal.statuses;

import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;

public class Slodged extends Status {

	private static String name = "Slodged";
	private static String descr = "Slowed";
	
	private float slow;
	
	public Slodged(PlayState state, float i, float slow, BodyData p, BodyData v) {
		super(state, i, name, descr, false, true, p, v);
		this.slow = slow;
	}
	
	@Override
	public void statChanges(){
		inflicted.setBonusAirSpeed(-slow);
		inflicted.setBonusGroundSpeed(-slow);
	}
}
