package com.mygdx.hadal.equip.modeMods;

import com.mygdx.hadal.equip.artifacts.Artifact;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;

public class PlayerGiant extends Artifact {

	private static final int SLOT_COST = 0;
	private static final float PLAYER_SCALE = 0.8f;

	public PlayerGiant() { super(SLOT_COST); }

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new Status(state, p) {

			@Override
			public void onInflict() { p.getPlayer().changeScaleModifier(PLAYER_SCALE); }
		};
	}
}
