package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.ParticleToggleable;
import com.mygdx.hadal.utils.Stats;

import static com.mygdx.hadal.utils.Constants.PRIORITY_PROC;

public class DeplorableApparatus extends Artifact {

	private static final int slotCost = 3;
	
	private static final float hpRegen = 13.0f;
	private static final float procCd = 2.0f;
	
	public DeplorableApparatus() {
		super(slotCost);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new ParticleToggleable(state, p, Particle.REGEN) {

			private float procCdCount = procCd;
			private float lastHp;
			@Override
			public void timePassing(float delta) {
				super.timePassing(delta);

				if (procCdCount < procCd) {
					procCdCount += delta;
				}

				if (lastHp > p.getCurrentHp()) {
					procCdCount = 0;
				}
				lastHp = p.getCurrentHp();

				boolean activated = procCdCount >= procCd && p.getCurrentHp() < p.getStat(Stats.MAX_HP);
				setActivated(activated);

				if (activated) {
					p.regainHp(hpRegen * delta, p, true, DamageTag.REGEN);
				}
			}
		}.setPriority(PRIORITY_PROC);
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) hpRegen),
				String.valueOf((int) procCd)};
	}
}
