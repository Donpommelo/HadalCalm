package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.equip.Equipable;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;

public class CrownofThorns extends Artifact {

	private final static int statusNum = 1;
	private final static int slotCost = 2;

	public CrownofThorns() {
		super(slotCost, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new Status(state, b) {
			
			@Override
			public void onReload(Equipable tool) {
				
			}
		};
		return enchantment;
	}
}
