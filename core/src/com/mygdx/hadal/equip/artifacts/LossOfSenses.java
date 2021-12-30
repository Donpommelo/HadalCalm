package com.mygdx.hadal.equip.artifacts;

import com.badlogic.gdx.math.MathUtils;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.utils.Stats;

import static com.mygdx.hadal.utils.Constants.PRIORITY_LAST;

public class LossOfSenses extends Artifact {

	private static final int slotCost = 1;

	private static final float bonusHpMin = 0.25f;
	private static final float bonusHpMax = 1.0f;

	public LossOfSenses() {
		super(slotCost);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new Status(state, p) {

			private float damageStored;
			private float bonusHp;
			@Override
			public void onInflict() {
				bonusHp = MathUtils.random(bonusHpMin, bonusHpMax);
			}

			@Override
			public void timePassing(float delta) {
				p.setCurrentHp(p.getStat(Stats.MAX_HP));
			}
			
			@Override
			public float onReceiveDamage(float damage, BodyData perp, Hitbox damaging, DamageTypes... tags) {
				damageStored += damage;
				if (damageStored >= p.getStat(Stats.MAX_HP) * (1.0f + bonusHp)) {
					return 9999;
				}
				return 0;
			}

			@Override
			public float onHeal(float damage, BodyData perp, DamageTypes... tags) {
				damageStored = Math.max(damageStored - damage, 0.0f);
				return 0;
			}
		}.setPriority(PRIORITY_LAST);
	}
}
