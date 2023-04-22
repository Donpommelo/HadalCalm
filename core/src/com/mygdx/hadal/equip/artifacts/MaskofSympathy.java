package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.statuses.Status;

import static com.mygdx.hadal.constants.Constants.PRIORITY_SCALE;

public class MaskofSympathy extends Artifact {

	private static final int SLOT_COST = 1;
	
	private static final float AMOUNT = 0.5f;
	private static final float FUEL_CD = 2.0f;

	public MaskofSympathy() {
		super(SLOT_COST);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new Status(state, p) {

			@Override
			public float onReceiveDamage(float damage, BodyData perp, Hitbox damaging, DamageSource source, DamageTag... tags) {
				if (!perp.equals(p) && damage > 0 && perp instanceof PlayerBodyData playerData) {
					playerData.fuelSpend(damage * AMOUNT);
					playerData.getPlayer().getFuelHelper().setFuelRegenCdCount(FUEL_CD);
				}
				return damage;
			}
		}.setPriority(PRIORITY_SCALE);
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) (AMOUNT * 100))};
	}
}
