package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.battle.attacks.special.Emote;
import com.mygdx.hadal.constants.Stats;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;

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
				String.valueOf((int) Emote.EMOTE_EXPLODE_DAMAGE)};
	}
}
