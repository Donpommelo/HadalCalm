package com.mygdx.hadal.equip.artifacts;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.equip.Equippable;
import com.mygdx.hadal.schmucks.bodies.hitboxes.SyncedAttack;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;

public class WrathoftheFrogman extends Artifact {

	private static final int slotCost = 2;
	
	private static final float procCd = 0.8f;

	public WrathoftheFrogman() {
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
			public void whileAttacking(float delta, Equippable tool) {
				if (tool.isReloading()) { return; }
				
				if (procCdCount >= procCd) {
					procCdCount -= procCd;

					SyncedAttack.HOMING_MISSILE.initiateSyncedAttackSingle(state, inflicted.getSchmuck(),
							inflicted.getSchmuck().getPixelPosition(), new Vector2(0, 1));
				}
			}
		};
	}
}
