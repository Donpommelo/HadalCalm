package com.mygdx.hadal.statuses;

import java.util.Arrays;

import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;

/**
 * Type resistance gives a unit resistance (or amplification) to a specificied damate type(s)
 * @author Zachary Tu
 */
public class TypeResistance extends Status {

	//this is the amount the damage is resisted by
	private float power;
	
	//this is the type of damage that is resisted.
	private DamageTypes resisted;
	
	public TypeResistance(PlayState state, float i, float power, DamageTypes resisted, BodyData p, BodyData v) {
		super(state, i, false, p, v);
		this.resisted = resisted;
		this.power = power;
	}
	
	public TypeResistance(PlayState state, float power, DamageTypes resisted, BodyData i) {
		super(state, i);
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
