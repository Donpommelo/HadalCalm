package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.battle.attacks.artifact.PepperActivate;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;

public class Pepper extends Artifact {

	private static final int SLOT_COST = 1;
	
	private static final float DAMAGE = PepperActivate.DAMAGE;
	private static final float PROC_CD = 1.5f;

	public Pepper() {
		super(SLOT_COST);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new Status(state, p) {
			
			private float procCdCount = PROC_CD;
			@Override
			public void timePassing(float delta) {
				if (procCdCount < PROC_CD) {
					procCdCount += delta;
				}
				if (procCdCount >= PROC_CD) {
					procCdCount -= PROC_CD;
					SyncedAttack.PEPPER_ARTIFACT.initiateSyncedAttackNoHbox(state, p.getPlayer(), p.getPlayer().getPosition(), true);
				}
			}
		}.setUserOnly(true);
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf(PROC_CD),
				String.valueOf((int) DAMAGE)};
	}
}
