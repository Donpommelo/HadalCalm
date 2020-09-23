package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;

public class FallacyofFlesh extends Artifact {

	private final static int statusNum = 1;
	private final static int slotCost = 1;
	
	private final float amountEnemy = 1.0f;
	private final float amountPlayer = 10.0f;
	
	public FallacyofFlesh() {
		super(slotCost, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new Status(state, b) {

			@Override
			public void onKill(BodyData vic) {
				SoundEffect.MAGIC1_ACTIVE.playUniversal(state, inflicted.getSchmuck().getPixelPosition(), 0.4f, false);
				
				if (vic instanceof PlayerBodyData) {
					((PlayerBodyData) inflicted).getActiveItem().gainCharge(amountPlayer);

				} else {
					((PlayerBodyData) inflicted).getActiveItem().gainCharge(amountEnemy);
				}
			}
		};
		return enchantment;
	}
}
