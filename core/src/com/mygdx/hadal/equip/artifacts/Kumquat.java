package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Regeneration;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.utils.Stats;

public class Kumquat extends Artifact {

	private static final int statusNum = 1;
	private static final int slotCost = 1;

	private static final float regenDuration = 1.0f;
	private static final float regenAmount = 0.35f;

	public Kumquat() {
		super(slotCost, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new Status(state, b) {

			private boolean activated;

			@Override
			public void timePassing(float delta) {
				if (!activated) {
					if (inflicted.getCurrentHp() / inflicted.getStat(Stats.MAX_HP) <= 0.5f) {
						activated = true;

						float healAmount = regenAmount * inflicted.getStat(Stats.MAX_HP) / regenDuration;

						SoundEffect.EATING.playUniversal(state, inflicted.getSchmuck().getPixelPosition(), 0.8f, false);
						inflicted.addStatus(new Regeneration(state, regenDuration, inflicted, inflicted, healAmount));
					}
				}
			}
		};
		return enchantment;
	}
}
