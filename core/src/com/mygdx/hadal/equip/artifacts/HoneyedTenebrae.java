package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;

import static com.mygdx.hadal.constants.StatusPriority.PRIORITY_PRE_SCALE_HONEYED_TENEBRAE;

public class HoneyedTenebrae extends Artifact {

	private static final int SLOT_COST = 2;

	private static final float DAMAGE_RESISTANCE = 0.7f;

	public HoneyedTenebrae() {
		super(SLOT_COST);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new Status(state, p) {
			
			@Override
			public float onReceiveDamage(float damage, BodyData perp, Hitbox damaging, DamageSource source, DamageTag... tags) {
				if (damage >= p.getCurrentHp()) {
					return damage * (1.0f - DAMAGE_RESISTANCE);
				}
				return damage;
			}
		}.setPriority(PRIORITY_PRE_SCALE_HONEYED_TENEBRAE);
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) (DAMAGE_RESISTANCE * 100))};
	}
}
