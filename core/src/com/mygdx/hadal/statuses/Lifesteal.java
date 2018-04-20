package com.mygdx.hadal.statuses;

import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;

public class Lifesteal extends Status {

	private static String name = "Lifesteal";
	private float power;

	public Lifesteal(PlayState state, int i, float amount, BodyData p, BodyData v, int pr) {
		super(state, i, name, false, false, true, true, p, v, pr);
		this.power = amount;
	}
	
	public Lifesteal(PlayState state, float amount, BodyData p, BodyData v, int pr) {
		super(state, 0, name, true, false, false, false, p, v, pr);
		this.power = amount;
	}
	
	@Override
	public float onDealDamage(float damage, BodyData vic, DamageTypes... tags) {

		perp.regainHp(power * damage, perp, true, DamageTypes.LIFESTEAL);
		
		return damage;
	}
	
}
