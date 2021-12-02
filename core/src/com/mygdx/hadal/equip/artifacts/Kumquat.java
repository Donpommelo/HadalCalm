package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Regeneration;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.utils.Stats;

public class Kumquat extends Artifact {

	private static final int slotCost = 1;

	private static final float regenDuration = 1.0f;
	private static final float regenAmount = 0.35f;

	public Kumquat() {
		super(slotCost);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new Status(state, p) {

			private boolean activated;
			@Override
			public void timePassing(float delta) {
				if (!activated) {
					if (p.getCurrentHp() / p.getStat(Stats.MAX_HP) <= 0.5f) {
						activated = true;

						float healAmount = regenAmount * p.getStat(Stats.MAX_HP) / regenDuration;

						SoundEffect.EATING.playUniversal(state, p.getSchmuck().getPixelPosition(), 0.8f, false);
						p.addStatus(new Regeneration(state, regenDuration, p, p, healAmount));
					}
				}
			}
		};
	}
}
