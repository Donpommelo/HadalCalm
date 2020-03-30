package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.statuses.Status;

public class Kermungler extends Artifact {

	private final static int statusNum = 1;
	private final static int slotCost = 1;
	
	private final static float damageVariance = 0.5f;
	private final static float damageAmp = 0.1f;
	private final static float damageRes = 0.1f;
	
	public Kermungler() {
		super(slotCost, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new Status(state, b) {
			
			@Override
			public float onDealDamage(float damage, BodyData perp, DamageTypes... tags) {
				float finalDamage = damage;
				finalDamage += damage * damageAmp;
				finalDamage += damage * (-damageVariance + Math.random() * 2 * damageVariance);
				return finalDamage;
			}
			
			@Override
			public float onReceiveDamage(float damage, BodyData perp, DamageTypes... tags) {
				float finalDamage = damage;
				finalDamage -= damage * damageRes;
				finalDamage += damage * (-damageVariance + Math.random() * 2 * damageVariance);
				return finalDamage;
			}
		};
		return enchantment;
	}
}
