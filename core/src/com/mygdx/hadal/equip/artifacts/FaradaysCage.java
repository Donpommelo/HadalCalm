package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.statuses.Status;

import static com.mygdx.hadal.utils.Constants.PRIORITY_MULT_SCALE;

public class FaradaysCage extends Artifact {

	private static final int slotCost = 2;
	
	private final float amount = 0.5f;
	
	public FaradaysCage() {
		super(slotCost);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new Status(state, p) {
			
			@Override
			public float onReceiveDamage(float damage, BodyData perp, Hitbox damaging, DamageTypes... tags) {
				if (damage > 0) {
					float amountReduced = damage * amount;
					if (p.getCurrentFuel() >= amountReduced) {
						p.fuelSpend(amountReduced);
						return damage - amountReduced;
					} else {
						float newDamage = damage - p.getCurrentFuel();
						p.fuelSpend(p.getCurrentFuel());
						return newDamage;
					}
				}
				return damage;
			}
		}.setPriority(PRIORITY_MULT_SCALE);
	}
}
