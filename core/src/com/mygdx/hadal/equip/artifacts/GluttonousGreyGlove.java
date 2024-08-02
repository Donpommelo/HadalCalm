package com.mygdx.hadal.equip.artifacts;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.constants.Constants;

public class GluttonousGreyGlove extends Artifact {

	private static final int SLOT_COST = 2;

	private static final float HEAL = 0.25f;
	private static final float CHANCE_PLAYER = 1.0f;
	private static final float CHANCE_MONSTER = 0.2f;

	public GluttonousGreyGlove() {
		super(SLOT_COST);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new Status(state, p) {
			
			@Override
			public void onKill(BodyData vic, DamageSource source, DamageTag... tags) {
				if (MathUtils.randomBoolean(CHANCE_PLAYER) && vic instanceof PlayerBodyData) {
					SyncedAttack.GLUTTONOUS_GREY_GLOVE.initiateSyncedAttackNoHbox(state, p.getPlayer(), new Vector2(), true);
					SyncedAttack.PICKUP.initiateSyncedAttackSingle(state, vic.getSchmuck(), vic.getSchmuck().getPixelPosition(),
							vic.getSchmuck().getLinearVelocity(), Constants.PICKUP_HEALTH, HEAL);
				} else if (MathUtils.randomBoolean(CHANCE_MONSTER)) {
					SyncedAttack.GLUTTONOUS_GREY_GLOVE.initiateSyncedAttackNoHbox(state, p.getPlayer(), new Vector2(), true);
					SyncedAttack.PICKUP.initiateSyncedAttackSingle(state, vic.getSchmuck(), vic.getSchmuck().getPixelPosition(),
							vic.getSchmuck().getLinearVelocity(), Constants.PICKUP_HEALTH, HEAL);
				}
			}
		}.setServerOnly(true);
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) (CHANCE_PLAYER * 100)),
				String.valueOf((int) (CHANCE_MONSTER * 100)),
				String.valueOf((int) (HEAL * 100))};
	}
}
