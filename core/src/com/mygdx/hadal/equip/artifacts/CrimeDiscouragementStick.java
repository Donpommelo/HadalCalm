package com.mygdx.hadal.equip.artifacts;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.battle.attacks.active.StickGrenade;
import com.mygdx.hadal.equip.Equippable;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;

public class CrimeDiscouragementStick extends Artifact {

	private static final int SLOT_COST = 2;

	private static final float PROC_CD = 1.25f;
	private static final float GRENADE_VELOCITY = 25.0f;

	public CrimeDiscouragementStick() {
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
					startVelo.set(0, GRENADE_VELOCITY).setAngleDeg(p.getPlayer().getMouseHelper().getAttackAngle() + 180);

					SyncedAttack.STICK_GRENADE.initiateSyncedAttackSingle(state, inflicted.getSchmuck(),
							inflicted.getSchmuck().getPixelPosition(), startVelo);
				}
			}
		};
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf(PROC_CD),
				String.valueOf((int) StickGrenade.STICK_GRENADE_EXPLOSION_DAMAGE)};
	}
}
