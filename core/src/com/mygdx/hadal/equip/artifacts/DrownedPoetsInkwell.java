package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;

public class DrownedPoetsInkwell extends Artifact {

	private static final int SLOT_COST = 1;

	public static final float INVIS_DURATION = 10.0f;

	public DrownedPoetsInkwell() {
		super(SLOT_COST);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new Status(state, p) {

			@Override
			public void onDeath(BodyData perp, DamageSource source, DamageTag... tags) {
				p.getPlayer().getUser().getEffectManager().setShowSpawnParticles(false);
			}
		}.setServerOnly(true);
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) INVIS_DURATION)};
	}
}
