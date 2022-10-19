package com.mygdx.hadal.equip.modeMods;

import com.mygdx.hadal.equip.artifacts.Artifact;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.constants.Stats;

public class PlayerSlide extends Artifact {

	private static final int SLOT_COST = 0;

	public PlayerSlide() { super(SLOT_COST); }

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new Status(state, p) {

			@Override
			public void statChanges() {
				inflicted.setStat(Stats.GROUND_DRAG, -1.0f);
			}
		};
	}
}
