package com.mygdx.hadal.equip.artifacts;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.statuses.Status;

public class MaskofSympathy extends Artifact {

	private static final int statusNum = 1;
	private static final int slotCost = 1;
	
	private static final float amount = 0.1f;
	
	public MaskofSympathy() {
		super(slotCost, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new Status(state, b) {

			@Override
			public float onReceiveDamage(float damage, BodyData perp, DamageTypes... tags) {

				if (!perp.equals(inflicted) && damage > 0) {
					perp.receiveDamage(damage * amount, new Vector2(0, 0), inflicted, false);
				}
				return damage;
			}
		};
		return enchantment;
	}
}
