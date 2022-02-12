package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.schmucks.SyncType;
import com.mygdx.hadal.schmucks.entities.ParticleEntity;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.StatusComposite;
import com.mygdx.hadal.utils.Stats;

public class AmdahlsLotus extends Artifact {

	private static final int slotCost = 1;

	private static final float timeThreshold = 0.2f;
	private static final float hpRegenBuff = 40.0f;
	private static final float fuelRegenBuff = 15.0f;

	public AmdahlsLotus() {
		super(slotCost);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new Status(state, p) {

			private boolean activated;
			@Override
			public void playerCreate() {
				if (activated) {
					activateBuff();
				}
			}

			@Override
			public void timePassing(float delta) {
				if (!activated && state.getUiExtra().getMaxTimer() > 0) {
					if (state.getUiExtra().getTimer() <= state.getUiExtra().getMaxTimer() * timeThreshold) {
						activated = true;
						activateBuff();
					}
				}
			}

			private void activateBuff() {
				SoundEffect.MAGIC18_BUFF.playUniversal(state, p.getSchmuck().getPixelPosition(), 0.5f, false);
				new ParticleEntity(state, p.getSchmuck(), Particle.RING, 1.0f, 1.0f, true, SyncType.CREATESYNC).setScale(0.4f);

				p.addStatus(new StatusComposite(state, state.getUiExtra().getTimer(), false, p, p,
					new StatChangeStatus(state, Stats.FUEL_REGEN, fuelRegenBuff, p),
					new StatChangeStatus(state, Stats.HP_REGEN, hpRegenBuff, p)));
			}
		};
	}
}
