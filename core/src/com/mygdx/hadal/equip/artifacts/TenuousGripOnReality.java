package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.equip.Equippable;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Invisibility;
import com.mygdx.hadal.statuses.Status;

public class TenuousGripOnReality extends Artifact {

	private static final int statusNum = 1;
	private static final int slotCost = 3;

	public TenuousGripOnReality() {
		super(slotCost, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new Status(state, b) {

			@Override
			public void onReloadStart(Equippable tool) {
				if (tool.getClipLeft() == 0) {
					inflicted.addStatus(new Invisibility(state, tool.getReloadTime(), inflicted, inflicted));
				}
			}
		};
		return enchantment;
	}
}
