package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;

public class Plumpowder extends Artifact {

	private static final int SLOT_COST = 1;
	
	public Plumpowder() {
		super(SLOT_COST);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new Status(state, p) {

			private boolean activated;
			@Override
			public void timePassing(float delta) {
				if (!activated) {
					activated = true;
					p.getPlayer().getMagicHelper().getMagic().setCurrentChargePercent(1.0f);
				}
			}
		};
	}
}
