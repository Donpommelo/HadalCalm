package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.map.GameMode;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.statuses.Status;

public class BenthicDesires extends Artifact {

	private static final int statusNum = 1;
	private static final int slotCost = 1;

	private static final float speedThreshold = 2.0f;
	private static final float hpRegen = 9.0f;
	private static final float fuelRegen = 14.0f;

	public BenthicDesires() {
		super(slotCost, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new Status(state, b) {

			@Override
			public void timePassing(float delta) {
				boolean activated = inflicted.getSchmuck().getLinearVelocity().len2() < speedThreshold;
				if (!state.getMode().equals(GameMode.CAMPAIGN) && !state.getMode().equals(GameMode.BOSS)) {
					if (activated) {
						inflicted.regainHp(hpRegen * delta, inflicted, false, DamageTypes.REGEN);
					}
				}
				if (activated && inflicted instanceof PlayerBodyData playerData) {
					playerData.fuelGain(fuelRegen * delta);
				}
			}
		};
		return enchantment;
	}
}
