package com.mygdx.hadal.equip.actives;

import com.mygdx.hadal.equip.ActiveItem;
import com.mygdx.hadal.equip.Equipable;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;

public class Reloader extends ActiveItem {

	private final static String name = "Reloader";
	private final static float usecd = 0.0f;
	private final static float usedelay = 0.0f;
	private final static float maxCharge = 10.0f;
	
	public Reloader(Schmuck user) {
		super(user, name, usecd, usedelay, maxCharge, chargeStyle.byTime);
	}
	
	@Override
	public void useItem(PlayState state, PlayerBodyData user) {
		for (Equipable e : user.getMultitools()) {
			e.gainClip(100);
		}
	}
}
