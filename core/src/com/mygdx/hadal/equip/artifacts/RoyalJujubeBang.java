package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.statuses.Status;

public class RoyalJujubeBang extends Artifact {

	private final static String name = "Royal Jujube Bang";
	private final static String descr = "Deal bonus damage from a distance.";
	private final static String descrLong = "";
	private final static int statusNum = 1;
	
	public RoyalJujubeBang() {
		super(name, descr, descrLong, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new Status(state, name, descr, b) {
			
			@Override
			public float onDealDamage(float damage, BodyData vic, DamageTypes... tags) {
				
				float dist = vic.getSchmuck().getPosition().dst(inflicted.getSchmuck().getPosition());
				
				float boost = 1.0f;
				
				if (dist > 10) {
					boost = 1.2f;
				}
				if (dist > 15) {
					boost = 1.5f;
				}

				return damage * boost;
			}
		};
		return enchantment;
	}
}
