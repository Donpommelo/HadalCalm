package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;

public class RoyalJujubeBang extends Artifact {

	private static final int SLOT_COST = 2;
	
	private static final float DIST_THRESHOLD = 600.0f;
	private static final int CRIT_AMOUNT = 1;

	public RoyalJujubeBang() {
		super(SLOT_COST);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new Status(state, p) {

			@Override
			public int onCalcDealCrit(int crit, BodyData vic, Hitbox damaging, DamageSource source, DamageTag... tags) {
				float distSquared = vic.getSchmuck().getPixelPosition().dst2(p.getSchmuck().getPixelPosition());
				if (distSquared > DIST_THRESHOLD * DIST_THRESHOLD) {
					return crit + CRIT_AMOUNT;
				}
				return crit;
			}
		};
	}
}
