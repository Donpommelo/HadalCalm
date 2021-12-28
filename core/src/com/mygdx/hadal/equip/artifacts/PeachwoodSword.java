package com.mygdx.hadal.equip.artifacts;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.schmucks.bodies.hitboxes.SyncedAttack;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;

public class PeachwoodSword extends Artifact {

	private static final int slotCost = 1;
	
	private static final float spiritDamageEnemy = 15.0f;
	private static final float spiritDamagePlayer = 50.0f;

	public PeachwoodSword() {
		super(slotCost);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new Status(state, p) {
			
			@Override
			public void onKill(BodyData vic) {
				if (vic instanceof PlayerBodyData) {
					SyncedAttack.VENGEFUL_SPIRIT.initiateSyncedAttackMulti(state, p.getSchmuck(), new Vector2[] {vic.getSchmuck().getPixelPosition()},
							new Vector2[] {}, 0.0f, 0.0f, spiritDamagePlayer);
				} else {
					SyncedAttack.VENGEFUL_SPIRIT.initiateSyncedAttackMulti(state, p.getSchmuck(), new Vector2[] {vic.getSchmuck().getPixelPosition()},
							new Vector2[] {}, 0.0f, 0.0f, spiritDamageEnemy);
				}
			}
			
			@Override
			public void onDeath(BodyData perp) {
				SyncedAttack.VENGEFUL_SPIRIT.initiateSyncedAttackMulti(state, p.getSchmuck(), new Vector2[] {p.getSchmuck().getPixelPosition()},
						new Vector2[] {}, 0.0f, 0.0f, spiritDamagePlayer);
			}
		};
	}
}
