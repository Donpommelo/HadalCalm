package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.equip.ActiveItem;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;

public class CatalogofWant extends Artifact {

	private static final int slotCost = 1;
	
	private static final float hpConversion = 4.0f;
	private static final float procCd = 2.0f;

	public CatalogofWant() {
		super(slotCost);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new Status(state, p) {

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
						if (p.getCurrentHp() > hpCost) {
							SoundEffect.MAGIC1_ACTIVE.playUniversal(p.getSchmuck().getState(), p.getSchmuck().getPixelPosition(), 0.4f, false);

							p.setCurrentHp(p.getCurrentHp() - hpCost);
							tool.gainChargeByPercent(1.0f);

							procCdCount = 0.0f;
						}
					}
				}
			}
		};
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) procCd),
				String.valueOf((int) hpConversion)};
	}
}
