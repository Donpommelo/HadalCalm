package com.mygdx.hadal.equip.misc;

import com.mygdx.hadal.equip.MeleeWeapon;
import com.mygdx.hadal.schmucks.bodies.Schmuck;

public class NothingWeapon extends MeleeWeapon {

	private final static String name = "Nothing";
	private final static float swingCd = 1.0f;
	private final static float windup = 1.0f;
	private final static float momentum = 1.0f;
	
	public NothingWeapon(Schmuck user) {
		super(user, name, swingCd, windup, momentum);
	}
}
