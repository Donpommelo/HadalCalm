package com.mygdx.hadal.equip.artifacts;

import java.util.Arrays;

import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.statuses.Status;

public class MangroveTalisman extends Artifact {

	private final static String name = "Mangrove Talisman";
	private final static String descr = "Poison Resistance";
	private final static String descrLong = "";
	private final static int statusNum = 1;
	
	private final static float res = 0.25f;
	
	public MangroveTalisman() {
		super(name, descr, descrLong, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new Status(state, name, descr, b) {
			
			@Override
			public float onReceiveDamage(float damage, BodyData perp, DamageTypes... tags) {
				if (Arrays.asList(tags).contains(DamageTypes.POISON)) {
					return damage * res;
				}
				return damage;
			}
		};
		return enchantment;
	}
}
