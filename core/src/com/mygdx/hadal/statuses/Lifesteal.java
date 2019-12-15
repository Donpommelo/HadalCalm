package com.mygdx.hadal.statuses;

import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;

public class Lifesteal extends Status {

	private static String name = "Lifesteal";
	private static String descr = "Heal when inflicting damage.";
	private float power;

	public Lifesteal(PlayState state, int i, float amount, BodyData p, BodyData v) {
		super(state, i, name, descr, false, p, v);
		this.power = amount;
	}
	
	public Lifesteal(PlayState state, float amount, BodyData i) {
		super(state, name, descr, i);
		this.power = amount;
	}
	
	@Override
	public float onDealDamage(float damage, BodyData vic, DamageTypes... tags) {

		inflicter.regainHp(power * damage, inflicter, true, DamageTypes.LIFESTEAL);
		
		return damage;
	}
}
