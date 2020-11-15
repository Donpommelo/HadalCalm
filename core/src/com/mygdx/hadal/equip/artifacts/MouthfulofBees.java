package com.mygdx.hadal.equip.artifacts;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.equip.WeaponUtils;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.statuses.Status;

public class MouthfulofBees extends Artifact {

	private static final int statusNum = 1;
	private static final int slotCost = 1;

	private static final float beeSpeed = 8.0f;
	private static final float damagePerBee = 20.0f;
	private static final int beesOnDeath = 5;
	
	public MouthfulofBees() {
		super(slotCost, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new Status(state, b) {
			
			
			@Override
			public float onReceiveDamage(float damage, BodyData perp, DamageTypes... tags) {
				
				if (damage > 0) {
					WeaponUtils.createBees(state, inflicted.getSchmuck().getPixelPosition(), inflicted.getSchmuck(), (int) (damage / damagePerBee), new Vector2(0, beeSpeed), false, inflicted.getSchmuck().getHitboxfilter());
				}

				return damage;
			}
			
			@Override
			public void onDeath(BodyData perp) {
				WeaponUtils.createBees(state, inflicted.getSchmuck().getPixelPosition(), inflicted.getSchmuck(), beesOnDeath, new Vector2(0, beeSpeed), false, inflicted.getSchmuck().getHitboxfilter());
			}
		};
		return enchantment;
	}
}
