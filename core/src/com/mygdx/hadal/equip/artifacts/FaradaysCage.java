package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.statuses.Status;

import static com.mygdx.hadal.constants.Constants.PRIORITY_MULT_SCALE;

public class FaradaysCage extends Artifact {

	private static final int SLOT_COST = 2;
	
	private static final float AMOUNT = 0.5f;
	private static final float FUEL_CD = 2.0f;

	public FaradaysCage() {
		super(SLOT_COST);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new Status(state, p) {
			
			@Override
			public float onReceiveDamage(float damage, BodyData perp, Hitbox damaging, DamageSource source, DamageTag... tags) {
				if (damage > 0) {
					float amountReduced = damage * AMOUNT;
					if (p.getCurrentFuel() >= amountReduced) {
						p.fuelSpend(amountReduced);
						p.getPlayer().getFuelHelper().setFuelRegenCdCount(FUEL_CD);
						return damage - amountReduced;
					} else {
						float newDamage = damage - p.getCurrentFuel();
						p.fuelSpend(p.getCurrentFuel());
						p.getPlayer().getFuelHelper().setFuelRegenCdCount(FUEL_CD);
						return newDamage;
					}
				}
				return damage;
			}
		}.setPriority(PRIORITY_MULT_SCALE).setUserOnly(true);
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) (AMOUNT * 100))};
	}
}
