package com.mygdx.hadal.equip.artifacts;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.StatusComposite;
import com.mygdx.hadal.utils.Stats;

public class RingoftheLamprey extends Artifact {

	private final static int statusNum = 1;
	private final static int slotCost = 2;
	
	private final static float lifesteal = 0.05f;
	private final static float damage = 2.5f;
	private final static float hpThreshold = 0.50f;
	
	public RingoftheLamprey() {
		super(slotCost, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new StatusComposite(state, b, 
				new Status(state, b) {
			
			private float procCdCount;
			private float procCd = 1.0f;
			
			@Override
			public void timePassing(float delta) {
				if (procCdCount >= procCd) {
					procCdCount -= procCd;
					
					if ((inflicter.getCurrentHp() / inflicter.getStat(Stats.MAX_HP)) >= hpThreshold) {
						inflicter.receiveDamage(damage, new Vector2(0, 0), inflicter, true);
					}
				}
				procCdCount += delta;
			}
			
			@Override
			public float onDealDamage(float damage, BodyData vic, DamageTypes... tags) {
				inflicter.regainHp(lifesteal * damage, inflicter, true, DamageTypes.LIFESTEAL);
				return damage;
			}
			
		});
		return enchantment;
	}
}
