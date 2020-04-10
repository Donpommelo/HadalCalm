package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.equip.ActiveItem;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.utils.Stats;

public class CatalogofWant extends Artifact {

	private final static int statusNum = 1;
	private final static int slotCost = 2;
	
	private final static float hpConversion = 0.75f;
	
	public CatalogofWant() {
		super(slotCost, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new Status(state, b) {
			
			@Override
			public void beforeActiveItem(ActiveItem tool) {
				float chargeRate = tool.chargePercent();
				
				if (chargeRate < 1.0f) {
					float hpCost = (1.0f - chargeRate) * inflicted.getStat(Stats.MAX_HP) * hpConversion;
					if (inflicted.getCurrentHp() > hpCost) {
						inflicted.setCurrentHp(inflicted.getCurrentHp() - hpCost);
						tool.gainChargeByPercent(1.0f);
						
						SoundEffect.MAGIC1_ACTIVE.playUniversal(inflicted.getSchmuck().getState(), inflicted.getSchmuck().getPixelPosition(), 0.4f, false);
					}
				}
			}
		};
		return enchantment;
	}
}
