package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;

public class HoneyedTenebrae extends Artifact {

	private static final int SLOT_COST = 2;

	private static final float DAMAGE_MULTIPLIER = 0.2f;

	public HoneyedTenebrae() {
		super(SLOT_COST);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new Status(state, p) {

			@Override
			public float onDealDamage(float damage, BodyData vic, Hitbox damaging, DamageSource source, DamageTag... tags) {
				vic.getSchmuck().getSpecialHpHelper().addConditionalHp(damage * DAMAGE_MULTIPLIER, DamageSource.HONEYED_TENEBRAE);
				return damage;
			}
		};
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int ) (DAMAGE_MULTIPLIER * 100))};
	}
}
