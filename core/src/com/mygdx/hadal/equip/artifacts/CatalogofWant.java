package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.equip.ActiveItem;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;

public class CatalogofWant extends Artifact {

	private static final int statusNum = 1;
	private static final int slotCost = 1;
	
	private static final float hpConversion = 4.0f;

	private static final float procCd = 2.0f;

	public CatalogofWant() {
		super(slotCost, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new Status(state, b) {

			private float procCdCount = procCd;
			@Override
			public void timePassing(float delta) {
				if (procCdCount < procCd) {
					procCdCount += delta;
				}
			}

			@Override
			public void beforeActiveItem(ActiveItem tool) {

				if (procCdCount >= procCd) {
					float chargeRate = tool.chargePercent();

					if (chargeRate < 1.0f) {
						float hpCost = (tool.getMaxCharge() - tool.getCurrentCharge()) * hpConversion;
						if (inflicted.getCurrentHp() > hpCost) {
							SoundEffect.MAGIC1_ACTIVE.playUniversal(inflicted.getSchmuck().getState(), inflicted.getSchmuck().getPixelPosition(), 0.4f, false);

							inflicted.setCurrentHp(inflicted.getCurrentHp() - hpCost);
							tool.gainChargeByPercent(1.0f);

							procCdCount = 0.0f;
						}
					}
				}


			}
		};
		return enchantment;
	}
}
