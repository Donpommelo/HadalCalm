package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.statuses.Status;

public class PelicanPlushToy extends Artifact {

	private final static String name = "Pelican Plush Toy";
	private final static String descr = "Improved Healing.";
	private final static String descrLong = "";
	private final static int statusNum = 1;
	
	private final float amount = 1.8f;
	
	public PelicanPlushToy() {
		super(name, descr, descrLong, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new Status(state, name, descr, b) {
			
			@Override
			public float onHeal(float damage, BodyData perp, DamageTypes... tags) { 
				return damage * amount; 
			}
		};
		return enchantment;
	}
}
