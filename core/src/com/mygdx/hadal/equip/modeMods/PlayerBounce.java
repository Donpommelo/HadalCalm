package com.mygdx.hadal.equip.modeMods;

import com.mygdx.hadal.equip.artifacts.Artifact;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;

public class PlayerBounce extends Artifact {

	private static final int slotCost = 0;

	private static final float bounce = 1.0f;

	public PlayerBounce() { super(slotCost); }

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new Status(state, p) {

			@Override
			public void onInflict() {
				p.getSchmuck().setRestitution(bounce);
			}

			@Override
			public void onRemove() {
				p.getSchmuck().setRestitution(0.0f);
			}
		};
	}
}
