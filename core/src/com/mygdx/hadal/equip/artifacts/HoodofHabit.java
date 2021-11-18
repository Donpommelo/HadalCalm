package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.statuses.Invisibility;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.utils.Stats;

public class HoodofHabit extends Artifact {

	private static final int statusNum = 1;
	private static final int slotCost = 1;
	
	private static final float hpThreshold = 0.5f;
	private static final float invisDuration = 10.0f;
	
	private static final float procCd = 1.0f;
	
	public HoodofHabit() {
		super(slotCost, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new Status(state, b) {
			
			private float procCdCount = procCd;
			
			@Override
			public void timePassing(float delta) {
				if (procCdCount < procCd) {
					procCdCount += delta;
				}
			}
			
			@Override
			public float onReceiveDamage(float damage, BodyData perp, DamageTypes... tags) {				
				if (procCdCount >= procCd) {
					if (inflicted.getCurrentHp() > hpThreshold * inflicted.getStat(Stats.MAX_HP) &&
						inflicted.getCurrentHp() - damage <= hpThreshold * inflicted.getStat(Stats.MAX_HP)) {
						procCdCount = 0;
						inflicted.addStatus(new Invisibility(state, invisDuration, inflicted, inflicted));
					}
				}
				return damage;
			}
		};
		return enchantment;
	}
}
