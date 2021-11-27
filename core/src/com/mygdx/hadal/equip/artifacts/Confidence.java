package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.utils.Stats;

public class Confidence extends Artifact {

	private static final int statusNum = 1;
	private static final int slotCost = 2;

	private static final float bonusDamage = 1.5f;
	private static final float hpThreshold = 0.9f;

	public Confidence() {
		super(slotCost, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new Status(state, b) {
			
			@Override
			public float onDealDamage(float damage, BodyData vic, Hitbox damaging, DamageTypes... tags) {
				if (inflicter.getCurrentHp() >= inflicter.getStat(Stats.MAX_HP) * hpThreshold) {
					return damage * bonusDamage;
				}
				return damage;	
			}
		};
		return enchantment;
	}
}
