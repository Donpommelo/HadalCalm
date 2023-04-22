package com.mygdx.hadal.equip.artifacts;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.constants.Stats;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.StatusComposite;

public class AuCourant extends Artifact {

	private static final int SLOT_COST = 3;
	
	private static final float BONUS_RELOAD_SPD = -0.20f;

	public AuCourant() {
		super(SLOT_COST);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new StatusComposite(state, p,
				new StatChangeStatus(state, Stats.RANGED_RELOAD, BONUS_RELOAD_SPD, p),
				new Status(state, p) {
			
			@Override
			public void timePassing(float delta) {
				for (int i = 0; i < p.getNumWeaponSlots(); i++) {
					if (i != p.getCurrentSlot()) {
						if (p.getMultitools()[i].getClipLeft() != p.getMultitools()[i].getClipSize()) {
							if (p.getMultitools()[i].reload(delta)) {
								SyncedAttack.ARTIFACT_AMMO_ACTIVATE.initiateSyncedAttackNoHbox(state, p.getPlayer(), new Vector2(), true);
							}
						}
					}
				}
			}
		}).setUserOnly(true);
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) -(BONUS_RELOAD_SPD * 100))};
	}
}
