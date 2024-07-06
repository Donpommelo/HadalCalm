package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.constants.Stats;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;

public class ContemptForLife extends Artifact {

	private static final int SLOT_COST = 1;

	private static final float HP_THRESHOLD = 0.33f;
	private static final int CRIT_AMOUNT = 1;

	public ContemptForLife() {
		super(SLOT_COST);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new Status(state, p) {

			@Override
			public int onCalcDealCrit(int crit, BodyData vic, Hitbox damaging, DamageSource source, DamageTag... tags) {
				if (vic.getCurrentHp() <= vic.getStat(Stats.MAX_HP) * HP_THRESHOLD) {
					return crit + CRIT_AMOUNT;
				}
				return crit;
			}
		};
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int ) (HP_THRESHOLD * 100))};
	}
}
