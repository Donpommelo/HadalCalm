package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.statuses.Status;

import java.util.Arrays;

public class LochShield extends Artifact {

	private static final int slotCost = 1;
	
	private static final float res = 0.6f;
	
	public LochShield() {
		super(slotCost);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new Status(state, p) {
			
			@Override
			public float onReceiveDamage(float damage, BodyData perp, Hitbox damaging, DamageTypes... tags) {
				if (Arrays.asList(tags).contains(DamageTypes.FIRE) || Arrays.asList(tags).contains(DamageTypes.EXPLOSIVE)) {
					return damage * res;
				}
				return damage;
			}
		};
	}
}
