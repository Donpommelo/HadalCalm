package com.mygdx.hadal.equip.artifacts;

import com.badlogic.gdx.math.MathUtils;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.utils.Constants;

public class GluttonousGreyGlove extends Artifact {

	private static final int slotCost = 2;
	private static final float heal = 0.25f;
	private static final float chancePlayer = 1.0f;
	private static final float chanceMonster = 0.2f;

	public GluttonousGreyGlove() {
		super(slotCost);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new Status(state, p) {
			
			@Override
			public void onKill(BodyData vic, DamageSource source) {
				if (MathUtils.randomBoolean(chancePlayer) && vic instanceof PlayerBodyData) {
					SyncedAttack.PICKUP.initiateSyncedAttackSingle(state, vic.getSchmuck(), vic.getSchmuck().getPixelPosition(),
							vic.getSchmuck().getLinearVelocity(), Constants.PICKUP_HEALTH, heal);
				} else if (MathUtils.randomBoolean(chanceMonster)) {
					SyncedAttack.PICKUP.initiateSyncedAttackSingle(state, vic.getSchmuck(), vic.getSchmuck().getPixelPosition(),
							vic.getSchmuck().getLinearVelocity(), Constants.PICKUP_HEALTH, heal);
				}
			}
		};
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) (chancePlayer * 100)),
				String.valueOf((int) (chanceMonster * 100)),
				String.valueOf((int) (heal * 100))};
	}
}
