package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.map.GameMode;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.ParticleToggleable;
import com.mygdx.hadal.constants.Stats;

public class BenthicDesires extends Artifact {

	private static final int SLOT_COST = 1;

	private static final float SPEED_THRESHOLD = 2.0f;
	private static final float MOVE_COOLDOWN = 1.0f;
	private static final float HP_REGEN = 9.0f;
	private static final float FUEL_REGEN = 14.0f;

	public BenthicDesires() {
		super(SLOT_COST);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new ParticleToggleable(state, p, Particle.REGEN) {

			private float count;
			@Override
			public void timePassing(float delta) {
				super.timePassing(delta);

				if (p.getSchmuck().getLinearVelocity().len2() >= SPEED_THRESHOLD) {
					count = MOVE_COOLDOWN;
				}
				count -= delta;

				boolean activated = count <= 0.0f && p.getCurrentHp() < p.getStat(Stats.MAX_HP);
				setActivated(activated);

				if (!GameMode.CAMPAIGN.equals(state.getMode()) && !GameMode.BOSS.equals(state.getMode())) {
					if (activated) {
						p.regainHp(HP_REGEN * delta, p, false, DamageTag.REGEN);
					}
				}
				if (activated) {
					p.fuelGain(FUEL_REGEN * delta);
				}
			}
		};
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) MOVE_COOLDOWN),
				String.valueOf((int) HP_REGEN),
				String.valueOf((int) FUEL_REGEN)};
	}
}
