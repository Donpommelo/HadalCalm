package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.map.GameMode;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.statuses.Status;

public class BenthicDesires extends Artifact {

	private static final int slotCost = 1;

	private static final float speedThreshold = 2.0f;
	private static final float hpRegen = 9.0f;
	private static final float fuelRegen = 14.0f;

	public BenthicDesires() {
		super(slotCost);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new Status(state, p) {

			@Override
			public void timePassing(float delta) {
				boolean activated = p.getSchmuck().getLinearVelocity().len2() < speedThreshold;
				if (!state.getMode().equals(GameMode.CAMPAIGN) && !state.getMode().equals(GameMode.BOSS)) {
					if (activated) {
						p.regainHp(hpRegen * delta, p, false, DamageTag.REGEN);
					}
				}
				if (activated) {
					p.fuelGain(fuelRegen * delta);
				}
			}
		};
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) hpRegen),
				String.valueOf((int) fuelRegen)};
	}
}
