package com.mygdx.hadal.equip.artifacts;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.constants.Stats;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Regeneration;
import com.mygdx.hadal.statuses.Status;

public class Kumquat extends Artifact {

	private static final int SLOT_COST = 1;

	private static final float HP_THRESHOLD = 0.5f;
	private static final float REGEN_DURATION = 1.0f;
	private static final float REGEN_AMOUNT = 0.35f;

	public Kumquat() {
		super(SLOT_COST);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new Status(state, p) {

			private boolean activated;
			@Override
			public void timePassing(float delta) {
				if (!activated) {
					if (p.getCurrentHp() / p.getStat(Stats.MAX_HP) <= HP_THRESHOLD) {
						activated = true;

						SyncedAttack.KUMQUAT.initiateSyncedAttackNoHbox(state, p.getPlayer(), new Vector2(), true);

						float healAmount = REGEN_AMOUNT * p.getStat(Stats.MAX_HP) / REGEN_DURATION;
						p.addStatus(new Regeneration(state, REGEN_DURATION, p, p, healAmount));
					}
				}
			}
		}.setUserOnly(true);
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) (HP_THRESHOLD * 100)),
				String.valueOf((int) (REGEN_AMOUNT * 100)),
				String.valueOf((int) REGEN_DURATION)};
	}
}
