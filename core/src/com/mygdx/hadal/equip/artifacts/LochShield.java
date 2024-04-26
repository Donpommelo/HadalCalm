package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.constants.Stats;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;

import static com.mygdx.hadal.constants.StatusPriority.PRIORITY_PRE_DEFAULT;

public class LochShield extends Artifact {

	private static final int SLOT_COST = 2;

	private static final float DAMAGE_THRESHOLD = 0.3f;
	private static final float DAMAGE_RES = 0.9f;

	public LochShield() {
		super(SLOT_COST);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new Status(state, p) {
			
			@Override
			public float onReceiveDamage(float damage, BodyData perp, Hitbox damaging, DamageSource source, DamageTag... tags) {

				float threshold = DAMAGE_THRESHOLD * p.getStat(Stats.MAX_HP);
				if (damage > threshold) {
					float excess = damage - threshold;
					damage -= (excess * DAMAGE_RES);
				}

				return damage;
			}
		}.setPriority(PRIORITY_PRE_DEFAULT);
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) (DAMAGE_THRESHOLD * 100)),
				String.valueOf((int) (DAMAGE_RES * 100))};
	}
}
