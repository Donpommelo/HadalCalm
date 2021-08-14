package com.mygdx.hadal.equip.modeMods;

import com.mygdx.hadal.equip.artifacts.Artifact;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;

public class PlayerBounce extends Artifact {

	private static final int statusNum = 1;
	private static final int slotCost = 0;

	private static final float bounce = 1.0f;

	public PlayerBounce() {
		super(slotCost, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new Status(state, b) {

			@Override
			public void onInflict() {
				inflicted.getSchmuck().setRestitution(bounce);
			}

			@Override
			public void onRemove() {
				inflicted.getSchmuck().setRestitution(0.0f);
			}
		};
		return enchantment;
	}
}
