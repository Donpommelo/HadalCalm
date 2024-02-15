package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.effects.HadalColor;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.equip.ActiveItem;
import com.mygdx.hadal.constants.SyncType;
import com.mygdx.hadal.schmucks.entities.ParticleEntity;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.ParticleToggleable;

public class Clepsydrae extends Artifact {

	private static final int SLOT_COST = 1;

	private static final float MAGIC_STORE_MULTIPLIER = 0.8f;
	private static final float SPEND_DURATION = 0.5f;

	public Clepsydrae() {
		super(SLOT_COST);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new ParticleToggleable(state, p) {

			private float storedCharge;
			private float chargeSpend;
			private float spentRate;
			@Override
			public void timePassing(float delta) {
				super.timePassing(delta);

				boolean activated = false;
				if (chargeSpend > 0.0f) {
					chargeSpend -= delta;
					p.getPlayer().getMagicHelper().getMagic().gainCharge(spentRate * delta);
					activated = true;
				}
				if (p.getPlayer().getMagicHelper().getMagic().chargePercent() == 1.0f) {
					if (storedCharge <= p.getPlayer().getMagicHelper().getMagic().getMaxCharge()) {
						storedCharge += (delta * MAGIC_STORE_MULTIPLIER);
					}
					activated = true;
				}
				setActivated(activated);
			}

			@Override
			public void afterActiveItem(ActiveItem tool) {
				if (storedCharge > 0.0f) {
					chargeSpend = SPEND_DURATION;
					spentRate = storedCharge / SPEND_DURATION;
					storedCharge = 0.0f;
				}
			}

			@Override
			public void createParticle() {
				setParticle(new ParticleEntity(state, inflicted.getSchmuck(), Particle.LIGHTNING_CHARGE, 0.0f, 0.0f,
						false, SyncType.NOSYNC).setColor(HadalColor.AMBER));
			}
		};
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) (MAGIC_STORE_MULTIPLIER * 100)),
				String.valueOf(SPEND_DURATION)};
	}
}
