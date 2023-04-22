package com.mygdx.hadal.equip.artifacts;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.equip.Equippable;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;

public class ForagersHive extends Artifact {

	private static final int SLOT_COST = 1;

	private static final float PROC_CD = 0.8f;
	private static final float BEE_SPEED = 15.0f;

	public ForagersHive() {
		super(SLOT_COST);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new Status(state, p) {
			
			private float procCdCount = PROC_CD;
			@Override
			public void timePassing(float delta) {
				if (procCdCount < PROC_CD) {
					procCdCount += delta;
				}
			}

			private final Vector2 startVelo = new Vector2();
			@Override
			public void whileAttacking(float delta, Equippable tool) {
				if (tool.isReloading()) { return; }
				
				if (procCdCount >= PROC_CD) {
					procCdCount -= PROC_CD;

					startVelo.set(0, BEE_SPEED).setAngleDeg(p.getPlayer().getMouseHelper().getAttackAngle() + 180);
					SyncedAttack.BEE_FORAGER.initiateSyncedAttackSingle(state, p.getSchmuck(), p.getSchmuck().getPixelPosition(),
							startVelo);
				}
			}
		};
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf(PROC_CD)};
	}
}
