package com.mygdx.hadal.equip.artifacts;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.battle.attacks.artifact.CommuterParasolActivate;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;

public class CommutersParasol extends Artifact {

	private static final int SLOT_COST = 2;
	
	private static final float LIFESPAN = CommuterParasolActivate.LIFESPAN;
	private static final float PROC_CD = 6.0f;

	public CommutersParasol() {
		super(SLOT_COST);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new Status(state, p) {
			
			private float procCdCount = PROC_CD;
			@Override
			public void timePassing(float delta) {
				while (procCdCount >= PROC_CD) {
					procCdCount -= PROC_CD;
					SyncedAttack.COMMUTERS_PARASOL.initiateSyncedAttackSingle(state, p.getPlayer(), new Vector2(), new Vector2());
				}
				procCdCount += delta;
			}
		}.setUserOnly(true);
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) PROC_CD),
				String.valueOf((int) LIFESPAN)};
	}
}
