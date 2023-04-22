package com.mygdx.hadal.equip.artifacts;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.constants.Stats;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.StatusComposite;

public class CastawaysTravelogue extends Artifact {

	private static final int SLOT_COST = 2;

	private static final float FUEL_REGEN = 30.0f;
	private static final float FUEL_DURATION = 1.0f;
	private static final float FUEL_THRESHOLD = 5.0f;

	private static final float PROC_CD = 7.5f;

	public CastawaysTravelogue() {
		super(SLOT_COST);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new StatusComposite(state, p,
				new Status(state, p) {
			
			private float procCdCount = PROC_CD;
			@Override
			public void timePassing(float delta) {
				if (procCdCount < PROC_CD) {
					procCdCount += delta;
				}

				if (procCdCount >= PROC_CD) {
					if (inflicted.getCurrentFuel() <= FUEL_THRESHOLD) {
						SyncedAttack.ARTIFACT_FUEL_ACTIVATE.initiateSyncedAttackNoHbox(state, p.getPlayer(), new Vector2(), true);

						p.addStatus(new StatChangeStatus(state, FUEL_DURATION, Stats.FUEL_REGEN, FUEL_REGEN, p, p));
						procCdCount = 0.0f;
					}
				}
			}
		}).setUserOnly(true);
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf(PROC_CD),
				String.valueOf((int) FUEL_REGEN)};
	}
}
