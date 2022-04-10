package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.utils.Stats;

public class FeelingofBeingWatched extends Artifact {

	private static final int slotCost = 1;
	
	private static final float fuelThreshold = 0.2f;
	private static final float bonusDamage = 1.4f;
	
	public FeelingofBeingWatched() {
		super(slotCost);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new Status(state, p) {
			
			@Override
			public float onDealDamage(float damage, BodyData vic, Hitbox damaging, DamageSource source, DamageTag... tags) {
				if (p.getCurrentFuel() <= p.getStat(Stats.MAX_FUEL) * fuelThreshold) {
					return damage * bonusDamage;
				}
				return damage;
			}
		};
	}
}
