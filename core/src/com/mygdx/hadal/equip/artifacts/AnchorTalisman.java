package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.statuses.Status;

public class AnchorTalisman extends Artifact {

	private final static String name = "Anchor Talisman";
	private final static String descr = "Damage Resistance When Grounded";
	private final static String descrLong = "";
	private final static int statusNum = 1;
	
	private final static float res = 0.5f;
	
	public AnchorTalisman() {
		super(name, descr, descrLong, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new Status(state, name, descr, b) {
			
			@Override
			public float onReceiveDamage(float damage, BodyData perp, DamageTypes... tags) {
				if (inflicted.getSchmuck().isGrounded()) {
					return damage * res;
				}
				return damage;
			}
		};
		return enchantment;
	}
}
