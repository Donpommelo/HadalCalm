package com.mygdx.hadal.equip.artifacts;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.equip.ActiveItem;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;

public class CatalogofWant extends Artifact {

	private static final int SLOT_COST = 1;
	
	private static final float HP_CONVERSION = 4.0f;
	private static final float PROC_CD = 2.0f;

	public CatalogofWant() {
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
			}

			@Override
			public void beforeActiveItem(ActiveItem tool) {
				if (procCdCount >= PROC_CD) {
					float chargeRate = tool.chargePercent();

					if (chargeRate < 1.0f) {
						float hpCost = (tool.getMaxCharge() - tool.getCurrentCharge()) * HP_CONVERSION;
						if (p.getCurrentHp() > hpCost) {
							SyncedAttack.ARTIFACT_MAGIC_ACTIVATE.initiateSyncedAttackNoHbox(state, p.getPlayer(), new Vector2(), true);

							p.setCurrentHp(p.getCurrentHp() - hpCost);
							tool.gainChargeByPercent(1.0f);

							procCdCount = 0.0f;
						}
					}
				}
			}
		}.setUserOnly(true);
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) PROC_CD),
				String.valueOf((int) HP_CONVERSION)};
	}
}
