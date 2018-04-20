package com.mygdx.hadal.statuses;

import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;

public class Slodged extends Status {

	private static String name = "Slodged";
	
	private final static float amp = 2.0f;
	private final static float slow = 0.99f;
	
	public Slodged(PlayState state, float i, BodyData p, BodyData v, int pr) {
		super(state, i, name, false, false, true, true, p, v, pr);
	}
	
	@Override
	public void statChanges(BodyData bodyData){
		bodyData.setBonusAirSpeed(-slow);
		bodyData.setBonusGroundSpeed(-slow);
	}
	
	@Override
	public float onReceiveDamage(float damage, BodyData perp, DamageTypes... tags) {
		damage *= amp;
		return damage;
	}

}
