package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.equip.Equippable;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Invisibility;
import com.mygdx.hadal.statuses.Status;

public class TenuousGripOnReality extends Artifact {

	private static final int slotCost = 3;

	private static final float bonusInvis = 1.5f;

	public TenuousGripOnReality() {
		super(slotCost);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new Status(state, p) {

			@Override
			public void onReloadStart(Equippable tool) {
				if (tool.getClipLeft() == 0) {
					p.addStatus(new Invisibility(state, tool.getReloadTime() * bonusInvis, p, p));
				}
			}
		};
	}
}
