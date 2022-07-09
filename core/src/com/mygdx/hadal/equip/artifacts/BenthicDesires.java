package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.map.GameMode;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.ParticleToggleable;
import com.mygdx.hadal.utils.Stats;

public class BenthicDesires extends Artifact {

	private static final int slotCost = 1;

	private static final float speedThreshold = 2.0f;
	private static final float moveCooldown = 1.0f;
	private static final float hpRegen = 9.0f;
	private static final float fuelRegen = 14.0f;

	public BenthicDesires() {
		super(slotCost);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new ParticleToggleable(state, p, Particle.REGEN) {

			private float count;
			@Override
			public void timePassing(float delta) {
				super.timePassing(delta);

				if (p.getSchmuck().getLinearVelocity().len2() >= speedThreshold) {
					count = moveCooldown;
				}
				count -= delta;

				boolean activated = count <= 0.0f && p.getCurrentHp() < p.getStat(Stats.MAX_HP);
				setActivated(activated);

				if (!GameMode.CAMPAIGN.equals(state.getMode()) && !GameMode.BOSS.equals(state.getMode())) {
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
				String.valueOf((int) moveCooldown),
				String.valueOf((int) hpRegen),
				String.valueOf((int) fuelRegen)};
	}
}
