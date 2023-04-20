package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.effects.HadalColor;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.constants.SyncType;
import com.mygdx.hadal.schmucks.entities.ParticleEntity;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.ParticleToggleable;
import com.mygdx.hadal.constants.Stats;

public class WretchedRebreather extends Artifact {

	private static final int SLOT_COST = 2;

	private static final float MAGIC_REGEN = 1.0f;
	private static final float FUEL_REGEN = 6.0f;

	public WretchedRebreather() {
		super(SLOT_COST);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new ParticleToggleable(state, p) {

			@Override
			public void timePassing(float delta) {
				super.timePassing(delta);

				boolean activated = false;
				if (p.getCurrentFuel() == p.getStat(Stats.MAX_FUEL) && p.getActiveItem().chargePercent() != 1.0f) {
					activated = true;
					p.getActiveItem().gainCharge(delta * MAGIC_REGEN);
				}
				if (p.getActiveItem().chargePercent() == 1.0f && p.getCurrentFuel() != p.getStat(Stats.MAX_FUEL)) {
					activated = true;
					p.fuelGain(FUEL_REGEN * delta);
				}
				setActivated(activated);
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
				String.valueOf((int) MAGIC_REGEN),
				String.valueOf((int) FUEL_REGEN)};
	}
}
