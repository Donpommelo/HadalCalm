package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.battle.WeaponUtils;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.utils.Stats;

public class TheFinger extends Artifact {

	private static final int slotCost = 1;
	
	private static final int pingDamage = 1;
	
	public TheFinger() {
		super(slotCost);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new StatChangeStatus(state, Stats.PING_DAMAGE, pingDamage, p);
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) WeaponUtils.EMOTE_EXPLODE_DAMAGE)};
	}
}
