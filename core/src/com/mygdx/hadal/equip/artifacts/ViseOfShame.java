package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;

public class ViseOfShame extends Artifact {

	private static final int slotCost = 2;
	private static final float sizeModifier = -0.6f;

	public ViseOfShame() {
		super(slotCost);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new Status(state, p) {
			
			@Override
			public void onInflict() {
				if (p.getPlayer().getBody() == null) {
					p.getPlayer().setScaleModifier(sizeModifier);
				}
			}
		};
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) -(sizeModifier * 100))};
	}
}
