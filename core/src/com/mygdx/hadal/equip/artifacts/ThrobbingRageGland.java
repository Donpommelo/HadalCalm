package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.Status;

public class ThrobbingRageGland extends Artifact {

	private final static String name = "Throbbing Rage Gland";
	private final static String descr = "Temporarily boosts speed and damage when taking damage.";
	private final static String descrLong = "";
	private final static int statusNum = 1;
	
	private final float damageFloor = 5;
	private final float dura = 3.0f;
	
	public ThrobbingRageGland() {
		super(name, descr, descrLong, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new Status(state, name, b) {
			
			@Override
			public float onReceiveDamage(float damage, BodyData perp, DamageTypes... tags) {
				if (damage > damageFloor) {
					inflicted.addStatus(new StatChangeStatus(state, dura, 4, 0.50f, perp, inflicted));
					inflicted.addStatus(new StatChangeStatus(state, dura, 5, 0.50f, perp, inflicted));
					inflicted.addStatus(new StatChangeStatus(state, dura, 21, 0.25f, perp, inflicted));
					inflicted.addStatus(new StatChangeStatus(state, dura, 23, 0.25f, perp, inflicted));
				}
				return damage;
			}
			
		};;
		return enchantment;
	}
}
