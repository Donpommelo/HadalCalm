package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.statuses.Status;

public class MouthbreatherTalisman extends Artifact {

	private final static String name = "Mouthbreather Talisman";
	private final static String descr = "Negates Self-Damage.";
	private final static String descrLong = "";
	private final static int statusNum = 1;
	
	public MouthbreatherTalisman() {
		super(name, descr, descrLong, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new Status(state, name, descr, b) {

			@Override
			public float onReceiveDamage(float damage, BodyData perp, DamageTypes... tags) {
				if (perp.equals(inflicted)) {
					return 0;				
				}
				return damage;
			}
		};
		return enchantment;
	}
}
