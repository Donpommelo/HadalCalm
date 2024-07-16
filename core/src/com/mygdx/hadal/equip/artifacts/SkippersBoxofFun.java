package com.mygdx.hadal.equip.artifacts;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.StatusComposite;

public class SkippersBoxofFun extends Artifact {

	private static final int SLOT_COST = 1;
	
	private static final float PROC_CD = 10.0f;

	public SkippersBoxofFun() {
		super(SLOT_COST);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new StatusComposite(state, p,
				new Status(state, p) {
			
			private float procCdCount;
			@Override
			public void timePassing(float delta) {
				
				if (procCdCount >= PROC_CD) {
					procCdCount -= PROC_CD;

					SyncedAttack.SKIPPERS_BOX_OF_FUN.initiateSyncedAttackNoHbox(state, p.getPlayer(), new Vector2(), true);
				}
				procCdCount += delta;
			}
		}).setUserOnly(true);
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) PROC_CD)};
	}
}
