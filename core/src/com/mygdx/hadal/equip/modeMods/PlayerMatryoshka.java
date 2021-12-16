package com.mygdx.hadal.equip.modeMods;

import com.mygdx.hadal.equip.artifacts.Artifact;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;

public class PlayerMatryoshka extends Artifact {

	private static final int slotCost = 0;

	private static final float[] SizeScaleList = {0.4f, 0.6f, 0.8f, 1.0f, 1.2f, 1.4f, 1.6f, 1.8f};

	public PlayerMatryoshka() { super(slotCost); }

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new Status(state, p) {

			@Override
			public void onInflict() {
				if (p.getPlayer().getUser() != null) {
					int livesLeft = Math.min(p.getPlayer().getUser().getScores().getLives(), SizeScaleList.length) - 1;
					p.getPlayer().setScaleModifier(SizeScaleList[livesLeft]);
				}
			}
		};
	}
}
