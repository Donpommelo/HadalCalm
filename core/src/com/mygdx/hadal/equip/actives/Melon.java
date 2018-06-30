package com.mygdx.hadal.equip.actives;

import com.mygdx.hadal.equip.ActiveItem;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;

public class Melon extends ActiveItem {

	private final static String name = "Melon";
	private final static float usecd = 0.0f;
	private final static float usedelay = 0.2f;
	private final static float maxCharge = 30.0f;
	
	private final static float duration = 5.0f;
	private final static float power = 8.0f;
	
	public Melon(Schmuck user) {
		super(user, name, usecd, usedelay, maxCharge, chargeStyle.byTime);
	}
	
	@Override
	public void useItem(PlayState state, BodyData user) {
		user.addStatus(new StatChangeStatus(state, duration, 2, power, user, user));
	}

}
