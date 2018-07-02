package com.mygdx.hadal.statuses;

import java.util.Arrays;

import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;

public class TypeResistance extends Status {

	private static String name = "Damage Resistance";
	private static String descr = "Damage Resistance";
	private float power;
	private DamageTypes resisted;
	
	public TypeResistance(PlayState state, int i, float power, DamageTypes resisted, BodyData p, BodyData v, int pr) {
		super(state, i, name, descr, false, false, true, true, p, v);
		this.resisted = resisted;
		this.power = power;
	}
	
	public TypeResistance(PlayState state, float power, DamageTypes resisted, BodyData i) {
		super(state, name, descr, i);
		this.resisted = resisted;
		this.power = power;
	}
	
	@Override
	public float onReceiveDamage(float damage, BodyData perp, DamageTypes... tags) {
		if (Arrays.asList(tags).contains(resisted)) {
			damage *= power;
		}
		return damage;
	}

}
