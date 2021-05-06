package com.mygdx.hadal.equip.artifacts;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.statuses.Status;

public class AncientSynapse extends Artifact {

	private static final int statusNum = 1;
	private static final int slotCost = 2;

	private static final float baseDegen = 2.0f;
	private static final float degen = 0.25f;

	private static final float procCd = 1 / 60f;
	
	public AncientSynapse() {
		super(slotCost, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new Status(state, b) {

			private float procCdCount;
			private float damageLeft = 0;
			
			@Override
			public void timePassing(float delta) {
				if (procCdCount >= procCd) {
					procCdCount -= procCd;
					if (damageLeft > 0.0f) {
						float damage = delta * (baseDegen + degen * damageLeft);
						inflicted.receiveDamage(damage, new Vector2(), inflicted, false);
						damageLeft -= damage;
					}
				}
				procCdCount += delta;
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
