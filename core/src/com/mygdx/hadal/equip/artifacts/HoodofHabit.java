package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.statuses.Invisibility;
import com.mygdx.hadal.statuses.Status;

public class HoodofHabit extends Artifact {

	private final static int statusNum = 1;
	private final static int slotCost = 1;
	
	private final static float hpThreshold = 25.0f;
	private final static float invisDuration = 8.0f;
	
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
					procCdCount = 0;
					if (inflicted.getCurrentHp() > hpThreshold && inflicted.getCurrentHp() - damage <= hpThreshold) {
						inflicted.addStatus(new Invisibility(state, invisDuration, inflicted, inflicted));
					}
				}
				return damage;
			}
		};
		return enchantment;
	}
}
