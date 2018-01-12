package com.mygdx.hadal.statuses;

import com.mygdx.hadal.schmucks.userdata.BodyData;

public class Lifesteal extends Status {

	private static String name = "Lifesteal";
	private float power;

	public Lifesteal(int i, float amount, BodyData p,	BodyData v, int pr) {
		super(i, name, false, false, true, true, p, v, pr);
		this.power = amount;
	}
	
	public Lifesteal(float amount, BodyData p, BodyData v, int pr) {
		super(0, name, true, false, false, false, p, v, pr);
		this.power = amount;
	}
	
	public float onDealDamage(float damage, BodyData vic) {

		perp.regainHp(power * damage);
		
		return damage;
	}
	
}
