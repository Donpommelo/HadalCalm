package com.mygdx.hadal.equip.artifacts;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.statuses.Status;

public class PrehistoricSynapse extends Artifact {

	private final static int statusNum = 1;
	private final static int slotCost = 2;
	
	private final static float degen = 8.0f;
	
	public PrehistoricSynapse() {
		super(slotCost, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new Status(state, b) {

			private float damageLeft = 0;
			
			@Override
			public void timePassing(float delta) {
				if (damageLeft > 0) {
					float damage = delta * degen;
					inflicted.receiveDamage(damage, new Vector2(0, 0), inflicted, inflicted.getCurrentTool(), false);
					damageLeft -= damage;
				}
			}
			
			@Override
			public float onReceiveDamage(float damage, BodyData perp, DamageTypes... tags) {
				
				damageLeft += damage;
				
				return 0;
			}
		};
		return enchantment;
	}
}
