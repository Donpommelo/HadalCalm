package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.battle.attacks.artifact.AmdhalsLotusActivate;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;

public class AmdahlsLotus extends Artifact {

	private static final int SLOT_COST = 1;

	private static final float TIME_THRESHOLD = 0.95f;
	private static final float HP_REGEN_BUFF = AmdhalsLotusActivate.HP_REGEN_BUFF;
	private static final float FUEL_REGEN_BUFF = AmdhalsLotusActivate.FUEL_REGEN_BUFF;

	public AmdahlsLotus() {
		super(SLOT_COST);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new Status(state, p) {

			private boolean activated;
			@Override
			public void timePassing(float delta) {
				activateBuff();
			}

			private void activateBuff() {
				if (!activated && state.getUiExtra().getMaxTimer() > 0) {
					if (state.getUiExtra().getTimer() <= state.getUiExtra().getMaxTimer() * TIME_THRESHOLD) {
						activated = true;
						SyncedAttack.AMDALHS_LOTUS.initiateSyncedAttackNoHbox(state, p.getPlayer(), p.getPlayer().getPixelPosition(), true);
					}
				}
			}
		}.setUserOnly(true);
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) (TIME_THRESHOLD * 100)),
				String.valueOf((int) HP_REGEN_BUFF),
				String.valueOf((int) FUEL_REGEN_BUFF)};
	}
}
