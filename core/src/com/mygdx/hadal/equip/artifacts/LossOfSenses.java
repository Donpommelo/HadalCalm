package com.mygdx.hadal.equip.artifacts;

import com.badlogic.gdx.math.MathUtils;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.constants.Stats;

import static com.mygdx.hadal.constants.Constants.PRIORITY_LAST;

public class LossOfSenses extends Artifact {

	private static final int SLOT_COST = 1;

	private static final float BONUS_HP_MIN = 0.25f;
	private static final float BONUS_HP_MAX = 1.0f;

	public LossOfSenses() {
		super(SLOT_COST);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new Status(state, p) {

			private float damageStored;
			private float bonusHp;
			@Override
			public void onInflict() {
				bonusHp = MathUtils.random(BONUS_HP_MIN, BONUS_HP_MAX);
			}

			@Override
			public void timePassing(float delta) {
				p.setCurrentHp(p.getStat(Stats.MAX_HP));
			}
			
			@Override
			public float onReceiveDamage(float damage, BodyData perp, Hitbox damaging, DamageSource source, DamageTag... tags) {
				damageStored += damage;
				if (damageStored >= p.getStat(Stats.MAX_HP) * (1.0f + bonusHp)) {
					return 9999;
				}
				return 0;
			}

			@Override
			public float onHeal(float damage, BodyData perp, DamageTag... tags) {
				damageStored = Math.max(damageStored - damage, 0.0f);
				return 0;
			}
		}.setPriority(PRIORITY_LAST);
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) (BONUS_HP_MIN * 100)),
				String.valueOf((int) (BONUS_HP_MAX * 100))};
	}
}
