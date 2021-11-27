package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.statuses.Status;

public class MouthbreatherCertificate extends Artifact {

	private static final int statusNum = 1;
	private static final int slotCost = 1;
	
	private static final float reduction = 0.1f;
	
	public MouthbreatherCertificate() {
		super(slotCost, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new Status(state, b) {

			@Override
			public float onReceiveDamage(float damage, BodyData perp, Hitbox damaging, DamageTypes... tags) {
				if (perp.equals(inflicted) && damage > 0) {
					return damage * reduction;				
				}
				return damage;
			}
		};
		return enchantment;
	}
}
