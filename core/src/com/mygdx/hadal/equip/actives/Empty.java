package com.mygdx.hadal.equip.actives;

import com.mygdx.hadal.equip.ActiveItem;
import com.mygdx.hadal.schmucks.bodies.Schmuck;

public class Empty extends ActiveItem {

	private final static String name = "Nothing";
	private final static float usecd = 0.0f;
	private final static float usedelay = 0.0f;
	private final static float maxCharge = 0.0f;
	
	public Empty(Schmuck user) {
		super(user, name, usecd, usedelay, maxCharge, chargeStyle.byDamage);
	}

}
