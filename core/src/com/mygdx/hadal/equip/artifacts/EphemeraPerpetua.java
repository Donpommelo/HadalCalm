package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;

public class EphemeraPerpetua extends Artifact {

	private static final int slotCost = 1;

	private final float amountEnemy = 1.0f;
	private final float amountPlayer = 10.0f;
	
	public EphemeraPerpetua() {
		super(slotCost);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new Status(state, p) {

			@Override
			public void onKill(BodyData vic, DamageSource source) {
				SoundEffect.MAGIC1_ACTIVE.playUniversal(state, p.getSchmuck().getPixelPosition(), 0.4f, false);

				if (vic instanceof PlayerBodyData) {
					p.getActiveItem().gainCharge(amountPlayer);
				} else {
					p.getActiveItem().gainCharge(amountEnemy);
				}
			}
		};
	}
}
