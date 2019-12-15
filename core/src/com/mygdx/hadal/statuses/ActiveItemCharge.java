package com.mygdx.hadal.statuses;

import com.mygdx.hadal.equip.ActiveItem.chargeStyle;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;

public class ActiveItemCharge extends Status {

	private static String name = "Active Item Charge";
	private static String descr = "Your Active Item Charges";

	public ActiveItemCharge(PlayState state, BodyData i) {
		super(state, name, descr, i);
	}
	
	@Override
	public float onDealDamage(float damage, BodyData vic, DamageTypes... tags) {

		if (inflicter instanceof PlayerBodyData) {
			if (((PlayerBodyData) inflicter).getActiveItem().getStyle().equals(chargeStyle.byDamage)) {
				((PlayerBodyData) inflicter).getActiveItem().gainCharge(damage);
			}
		}
		return damage;
	}
}
