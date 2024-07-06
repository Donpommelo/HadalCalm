package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.constants.Stats;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;

import static com.mygdx.hadal.constants.StatusPriority.PRIORITY_PRE_SCALE_FRACTURE_PLATE;

public class FracturePlate extends Artifact {

	private static final int SLOT_COST = 2;
	
	private static final float CD = 4.0f;
	private float procCdCount = 0;

	private static final float MAX_SHIELD = 0.2f;

	public FracturePlate() {
		super(SLOT_COST);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new Status(state, p) {

			@Override
			public void timePassing(float delta) {
				float maxShield = MAX_SHIELD * p.getStat(Stats.MAX_HP);
				float currentShield = p.getPlayer().getSpecialHpHelper().getShieldHp();
				if (currentShield < maxShield) {
					if (procCdCount >= 0) {
						procCdCount -= delta;
					}
					if (procCdCount < 0) {
						p.getPlayer().getSpecialHpHelper().addShield(maxShield - currentShield);
						SyncedAttack.FRACTURE_PLATE.initiateSyncedAttackNoHbox(state, p.getPlayer(), p.getPlayer().getPixelPosition(), true, 1.0f);
					}
				} else {
					procCdCount = CD;
				}
			}
		}.setPriority(PRIORITY_PRE_SCALE_FRACTURE_PLATE).setUserOnly(true);
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) CD),
				String.valueOf((int) (MAX_SHIELD * 100))};
	}
}
