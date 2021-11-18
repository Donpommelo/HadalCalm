package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.statuses.Regeneration;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.utils.Stats;

public class Blastema extends Artifact {

	private static final int statusNum = 1;
	private static final int slotCost = 2;
	
	private static final float regenCd = 5.0f;
	private static final float regen = 0.02f;
	
	public Blastema() {
		super(slotCost, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new Status(state, b) {
			
			private final float procCd = regenCd;
			private float procCdCount = procCd;
			@Override
			public void timePassing(float delta) {
				if (procCdCount < procCd) {
					procCdCount += delta;
				}
			}
			
			@Override
			public float onReceiveDamage(float damage, BodyData perp, DamageTypes... tags) {
				
				if (procCdCount >= procCd && damage > 0) {
					procCdCount -= procCd;
					
					inflicted.addStatus(new Regeneration(state, regenCd, inflicted, inflicted,
							regen * inflicted.getStat(Stats.MAX_HP)));
				}
				return damage;
			}
		};
		return enchantment;
	}
}
