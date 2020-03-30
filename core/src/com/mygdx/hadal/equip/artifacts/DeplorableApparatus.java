package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.StatusComposite;
import com.mygdx.hadal.utils.Stats;

public class DeplorableApparatus extends Artifact {

	private final static int statusNum = 1;
	private final static int slotCost = 3;
	
	private final static float hpReduction = -50.0f;
	private final static float hpRegen = 10.0f;
	
	private float procCdCount = 0;
	private final static float cd = 2.0f;
	
	public DeplorableApparatus() {
		super(slotCost, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new StatusComposite(state, b, 
				new StatChangeStatus(state, Stats.MAX_HP, hpReduction, b), 
				new Status(state, b) {
			
			@Override
			public void timePassing(float delta) {
				
				if (procCdCount >= 0) {
					procCdCount -= delta;
				}
				
				if (procCdCount < 0) {
					inflicted.regainHp(hpRegen * delta, inflicted, true, DamageTypes.REGEN);
				}
			}
			
			@Override
			public float onReceiveDamage(float damage, BodyData perp, DamageTypes... tags) {
				if (damage > 0) {
					procCdCount = cd;
				}
				return damage;
			}
		});
		return enchantment;
	}
}
