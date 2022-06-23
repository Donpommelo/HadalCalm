package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.statuses.Status;

import java.util.Arrays;

public class LochShield extends Artifact {

	private static final int slotCost = 1;

	private static final float fireRes = 0.4f;
	private static final float explosiveRes = 0.4f;

	public LochShield() {
		super(slotCost);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new Status(state, p) {
			
			@Override
			public float onReceiveDamage(float damage, BodyData perp, Hitbox damaging, DamageSource source, DamageTag... tags) {
				if (Arrays.asList(tags).contains(DamageTag.FIRE)) {
					return damage * (1.0f - fireRes);
				}
				if (Arrays.asList(tags).contains(DamageTag.EXPLOSIVE)) {
					return damage * (1.0f - explosiveRes);
				}
				return damage;
			}
		};
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) (fireRes * 100)),
				String.valueOf((int) (explosiveRes * 100))};
	}
}
