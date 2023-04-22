package com.mygdx.hadal.equip.artifacts;

import com.badlogic.gdx.math.MathUtils;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.statuses.Status;

public class Kermungler extends Artifact {

	private static final int SLOT_COST = 1;

	private static final float DAMAGE_VARIANCE = 0.5f;
	private static final float DAMAGE_AMP = 0.1f;
	private static final float DAMAGE_RES = 0.1f;
	
	public Kermungler() {
		super(SLOT_COST);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new Status(state, p) {
			
			@Override
			public float onDealDamage(float damage, BodyData perp, Hitbox damaging, DamageSource source, DamageTag... tags) {
				float finalDamage = damage;
				finalDamage += damage * DAMAGE_AMP;
				finalDamage += damage * (-DAMAGE_VARIANCE + MathUtils.random() * 2 * DAMAGE_VARIANCE);
				return finalDamage;
			}
			
			@Override
			public float onReceiveDamage(float damage, BodyData perp, Hitbox damaging, DamageSource source, DamageTag... tags) {
				float finalDamage = damage;
				finalDamage -= damage * DAMAGE_RES;
				finalDamage += damage * (-DAMAGE_VARIANCE + MathUtils.random() * 2 * DAMAGE_VARIANCE);
				return finalDamage;
			}
		};
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) (DAMAGE_VARIANCE * 100)),
				String.valueOf((int) (DAMAGE_AMP * 100)),
				String.valueOf((int) (DAMAGE_RES * 100))};
	}
}
