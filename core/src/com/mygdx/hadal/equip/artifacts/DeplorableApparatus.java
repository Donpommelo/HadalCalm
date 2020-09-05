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
	private final static int slotCost = 2;
	
	private final static float hpReduction = -40.0f;
	private final static float hpRegen = 12.0f;
	
	private final static float procCd = 1.0f;
	
	public DeplorableApparatus() {
		super(slotCost, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new StatusComposite(state, b, 
				new StatChangeStatus(state, Stats.MAX_HP, hpReduction, b), 
				new Status(state, b) {
			
			private float procCdCount = procCd;

			@Override
			public void timePassing(float delta) {
				
				if (procCdCount < procCd) {
					procCdCount += delta;
				}
				
				if (procCdCount >= procCd) {
					inflicted.regainHp(hpRegen * delta, inflicted, true, DamageTypes.REGEN);
				}
			}
			
			@Override
			public float onReceiveDamage(float damage, BodyData perp, DamageTypes... tags) {
				if (damage > 0) {
					procCdCount = 0;
				}
				return damage;
			}
		});
		return enchantment;
	}
}
