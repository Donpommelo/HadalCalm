package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.constants.Stats;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.*;

public class MangroveSeed extends Artifact {

	private static final int SLOT_COST = 1;
	
	private static final float STATUS_DURATION = 0.5f;
	private static final float BONUS_HP = 0.1f;

	public MangroveSeed() {
		super(SLOT_COST);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new StatusComposite(state, p,
				new StatChangeStatus(state, Stats.MAX_HP_PERCENT, BONUS_HP, p),
				new Status(state, p) {

					@Override
					public void beforeStatusInflict(Status status) {
						if (status instanceof Ablaze || status instanceof Blinded || status instanceof Slodged) {
							status.setDuration(STATUS_DURATION);
						}
					}
				});
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) (BONUS_HP * 100))};
	}
}
