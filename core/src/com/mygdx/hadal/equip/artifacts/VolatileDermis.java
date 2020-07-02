package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.statuses.Shocked;
import com.mygdx.hadal.statuses.Status;

public class VolatileDermis extends Artifact {

	private final static int statusNum = 1;
	private final static int slotCost = 1;
	
	private static final float procCd = 3.0f;
	
	private final static float chainDamage = 15.0f;
	private final static int chainRadius = 10;
	private final static int chainAmount = 3;
	
	public VolatileDermis() {
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
				if (procCdCount >= procCd && damage > 0) {
					procCdCount = 0;
					SoundEffect.THUNDER.playUniversal(state, inflicted.getSchmuck().getPixelPosition(), 0.5f, false);
					inflicted.addStatus(new Shocked(state, inflicted, inflicted, chainDamage, chainRadius, chainAmount, inflicted.getSchmuck().getHitboxfilter()));
				}
				return damage;
			}
		};
		return enchantment;
	}
}
