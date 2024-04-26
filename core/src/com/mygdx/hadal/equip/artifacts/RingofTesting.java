package com.mygdx.hadal.equip.artifacts;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.constants.Stats;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.StatusComposite;

public class RingofTesting extends Artifact {

	private static final int SLOT_COST = 0;

	private static final float HOVER_POW_REDUCTION = -0.5f;
	private static final float HOVER_COST_REDUCTION = -0.5f;
	private static final float MAX_CHARGE = 1.5f;
	private static final float MAX_BOOST = 100f;

	public RingofTesting() {
		super(SLOT_COST);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new StatusComposite(state, p,
				new StatChangeStatus(state, Stats.HOVER_POW, HOVER_POW_REDUCTION, p),
				new StatChangeStatus(state, Stats.HOVER_COST, HOVER_COST_REDUCTION, p),
				new Status(state, p) {

					private boolean charging;
					private float storedCharge;
					@Override
					public void timePassing(float delta) {
						if (charging) {
							if (storedCharge < MAX_CHARGE) {
								storedCharge += delta;
							}
						}
					}

					@Override
					public void startHover() {
						charging = true;
					}

					@Override
					public void endHover() {
						charging = false;

						if (storedCharge > 0.0f) {
							float power = Math.min(MAX_CHARGE, storedCharge) / MAX_CHARGE * MAX_BOOST;
							p.getPlayer().push(new Vector2(0, power));

							storedCharge = 0.0f;
						}
					}

					@Override
					public void statChanges() {
						p.setStat(Stats.HOVER_POW, -1.0f);
					}
				});
	}
}
