package com.mygdx.hadal.statuses;

import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;

public class Slodged extends Status {

	private static String name = "Slodged";
	private static String descr = "Slowed";
	
	private final static float amp = 2.0f;
	private final static float slow = 0.99f;
	
	public Slodged(PlayState state, float i, BodyData p, BodyData v) {
		super(state, i, name, descr, false, true, p, v);
	}
	
	@Override
	public void statChanges(){
		inflicted.setBonusAirSpeed(-slow);
		inflicted.setBonusGroundSpeed(-slow);
	}
	
	@Override
	public float onReceiveDamage(float damage, BodyData perp, DamageTypes... tags) {
		return damage * amp;
	}

}
