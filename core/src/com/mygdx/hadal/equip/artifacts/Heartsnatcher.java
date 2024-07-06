package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.constants.Stats;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;

public class Heartsnatcher extends Artifact {

	private static final int SLOT_COST = 1;

	private static final float BONUS_RELOAD_SPD = 0.15f;
	private static final int ARMOR_AMOUNT = 1;

	public Heartsnatcher() {
		super(SLOT_COST);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new StatChangeStatus(state, Stats.RANGED_RELOAD, BONUS_RELOAD_SPD, p) {

			public int onCalcArmorInflict(int armor, float damage, BodyData vic, Hitbox damaging, DamageSource source, DamageTag... tags) {
				return armor - ARMOR_AMOUNT;
			}
		};
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) (BONUS_RELOAD_SPD * 100)),
				String.valueOf(ARMOR_AMOUNT)};
	}
}
