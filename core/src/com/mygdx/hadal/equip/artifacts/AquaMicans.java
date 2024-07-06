package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;

import java.util.Arrays;

public class AquaMicans extends Artifact {

	private static final int SLOT_COST = 2;

	private static final int CRIT_AMOUNT = 1;

	public AquaMicans() {
		super(SLOT_COST);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new Status(state, p) {

			@Override
			public int onCalcDealCrit(int crit, BodyData vic, Hitbox damaging, DamageSource source, DamageTag... tags) {
				if (Arrays.asList(tags).contains(DamageTag.MAGIC)) {
					return crit + CRIT_AMOUNT;
				}
				return crit;
			}
		};
	}
}
