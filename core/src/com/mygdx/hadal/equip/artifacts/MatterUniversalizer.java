package com.mygdx.hadal.equip.artifacts;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;

public class MatterUniversalizer extends Artifact {

	private static final int SLOT_COST = 1;
	
	private static final float AMOUNT_ENEMY = 25.0f;
	private static final float AMOUNT_PLAYER = 75.0f;

	public MatterUniversalizer() {
		super(SLOT_COST);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new Status(state, p) {

			@Override
			public void onKill(BodyData vic, DamageSource source) {
				SyncedAttack.ARTIFACT_FUEL_ACTIVATE.initiateSyncedAttackNoHbox(state, p.getPlayer(), new Vector2(), true);

				if (vic instanceof PlayerBodyData) {
					p.fuelGain(AMOUNT_PLAYER);
				} else {
					p.fuelGain(AMOUNT_ENEMY);
				}
			}
		}.setUserOnly(true);
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) AMOUNT_PLAYER),
				String.valueOf((int) AMOUNT_ENEMY)};
	}
}
