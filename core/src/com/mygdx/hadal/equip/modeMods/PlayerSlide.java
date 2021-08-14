package com.mygdx.hadal.equip.modeMods;

import com.mygdx.hadal.equip.artifacts.Artifact;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.utils.Stats;

public class PlayerSlide extends Artifact {

	private static final int statusNum = 1;
	private static final int slotCost = 0;

	public PlayerSlide() {
		super(slotCost, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new Status(state, b) {

			@Override
			public void statChanges() {
				inflicted.setStat(Stats.GROUND_DRAG, -1.0f);
			}
		};
		return enchantment;
	}
}
