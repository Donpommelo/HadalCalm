package com.mygdx.hadal.equip.artifacts;

import com.badlogic.gdx.math.MathUtils;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.statuses.Status;

public class Kermungler extends Artifact {

	private static final int slotCost = 1;
	private static final float damageVariance = 0.5f;
	private static final float damageAmp = 0.1f;
	private static final float damageRes = 0.1f;
	
	public Kermungler() {
		super(slotCost);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new Status(state, p) {
			
			@Override
			public float onDealDamage(float damage, BodyData perp, Hitbox damaging, DamageTypes... tags) {
				float finalDamage = damage;
				finalDamage += damage * damageAmp;
				finalDamage += damage * (-damageVariance + MathUtils.random() * 2 * damageVariance);
				return finalDamage;
			}
			
			@Override
			public float onReceiveDamage(float damage, BodyData perp, Hitbox damaging, DamageTypes... tags) {
				float finalDamage = damage;
				finalDamage -= damage * damageRes;
				finalDamage += damage * (-damageVariance + MathUtils.random() * 2 * damageVariance);
				return finalDamage;
			}
		};
	}
}
