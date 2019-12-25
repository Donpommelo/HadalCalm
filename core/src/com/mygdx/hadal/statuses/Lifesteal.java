package com.mygdx.hadal.statuses;

import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;

public class Lifesteal extends Status {

	private float power;

	public Lifesteal(PlayState state, int i, float amount, BodyData p, BodyData v) {
		super(state, i, false, p, v);
		this.power = amount;
	}
	
	public Lifesteal(PlayState state, float amount, BodyData i) {
		super(state, i);
		this.power = amount;
	}
	
	@Override
	public float onDealDamage(float damage, BodyData vic, DamageTypes... tags) {

		inflicter.regainHp(power * damage, inflicter, true, DamageTypes.LIFESTEAL);
		
		return damage;
	}
}
