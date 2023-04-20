package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.ParticleToggleable;
import com.mygdx.hadal.constants.Stats;

import static com.mygdx.hadal.constants.Constants.PRIORITY_PROC;

public class DeplorableApparatus extends Artifact {

	private static final int SLOT_COST = 3;
	
	private static final float HP_REGEN = 13.0f;
	private static final float PROC_CD = 2.0f;
	
	public DeplorableApparatus() {
		super(SLOT_COST);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new ParticleToggleable(state, p, Particle.REGEN) {

			private float procCdCount = PROC_CD;
			private float lastHp;
			@Override
			public void timePassing(float delta) {
				super.timePassing(delta);

				if (procCdCount < PROC_CD) {
					procCdCount += delta;
				}

				if (lastHp > p.getCurrentHp()) {
					procCdCount = 0;
				}
				lastHp = p.getCurrentHp();

				boolean activated = procCdCount >= PROC_CD && p.getCurrentHp() < p.getStat(Stats.MAX_HP);
				setActivated(activated);

				if (activated) {
					p.regainHp(HP_REGEN * delta, p, true, DamageTag.REGEN);
				}
			}
		}.setPriority(PRIORITY_PROC).setUserOnly(true);
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) HP_REGEN),
				String.valueOf((int) PROC_CD)};
	}
}
