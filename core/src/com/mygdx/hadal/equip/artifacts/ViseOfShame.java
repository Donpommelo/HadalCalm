package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;

public class ViseOfShame extends Artifact {

	private static final int SLOT_COST = 2;
	private static final float SIZE_MODIFIER = -0.6f;

	public ViseOfShame() {
		super(SLOT_COST);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new Status(state, p) {
			
			@Override
			public void onInflict() {
				if (p.getPlayer().getBody() == null) {
					p.getPlayer().setScaleModifier(SIZE_MODIFIER);
				}
			}
		};
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) -(SIZE_MODIFIER * 100))};
	}
}
