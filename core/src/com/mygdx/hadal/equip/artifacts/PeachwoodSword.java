package com.mygdx.hadal.equip.artifacts;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;

public class PeachwoodSword extends Artifact {

	private static final int SLOT_COST = 1;
	
	private static final float SPIRIT_DAMAGE_ENEMY = 15.0f;
	private static final float SPIRIT_DAMAGE_PLAYER = 50.0f;

	public PeachwoodSword() {
		super(SLOT_COST);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new Status(state, p) {
			
			@Override
			public void onKill(BodyData vic, DamageSource source) {
				if (vic instanceof PlayerBodyData) {
					SyncedAttack.VENGEFUL_SPIRIT_PEACHWOOD.initiateSyncedAttackMulti(state, p.getSchmuck(), new Vector2(),
							new Vector2[] {vic.getSchmuck().getPixelPosition()}, new Vector2[] {},
							0.0f, 0.0f, SPIRIT_DAMAGE_PLAYER);
				} else {
					SyncedAttack.VENGEFUL_SPIRIT_PEACHWOOD.initiateSyncedAttackMulti(state, p.getSchmuck(), new Vector2(),
							new Vector2[] {vic.getSchmuck().getPixelPosition()}, new Vector2[] {},
							0.0f, 0.0f, SPIRIT_DAMAGE_ENEMY);
				}
			}
			
			@Override
			public void onDeath(BodyData perp, DamageSource source) {
				SyncedAttack.VENGEFUL_SPIRIT_PEACHWOOD.initiateSyncedAttackMulti(state, p.getSchmuck(), new Vector2(),
						new Vector2[] {p.getSchmuck().getPixelPosition()}, new Vector2[] {},
						0.0f, 0.0f, SPIRIT_DAMAGE_PLAYER);
			}
		}.setUserOnly(true);
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) SPIRIT_DAMAGE_PLAYER),
				String.valueOf((int) SPIRIT_DAMAGE_ENEMY)};
	}
}
