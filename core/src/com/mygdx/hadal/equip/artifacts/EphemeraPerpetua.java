package com.mygdx.hadal.equip.artifacts;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;

public class EphemeraPerpetua extends Artifact {

	private static final int SLOT_COST = 1;

	private static final float AMOUNT_ENEMY = 1.0f;
	private static final float AMOUNT_PLAYER = 10.0f;
	
	public EphemeraPerpetua() {
		super(SLOT_COST);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new Status(state, p) {

			@Override
			public void onKill(BodyData vic, DamageSource source) {
				SyncedAttack.ARTIFACT_MAGIC_ACTIVATE.initiateSyncedAttackNoHbox(state, p.getPlayer(), new Vector2(), true);

				if (vic instanceof PlayerBodyData) {
					p.getActiveItem().gainCharge(AMOUNT_PLAYER);
				} else {
					p.getActiveItem().gainCharge(AMOUNT_ENEMY);
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
