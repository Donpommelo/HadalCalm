package com.mygdx.hadal.equip.artifacts;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.battle.attacks.weapon.Bomb;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;

public class AnarchistsCookbook extends Artifact {

	private static final int SLOT_COST = 1;
	
	private static final float PROC_CD = 3.0f;
	
	public AnarchistsCookbook() {
		super(SLOT_COST);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment= new Status(state, p) {
			
			private float procCdCount;
			@Override
			public void timePassing(float delta) {
				if (procCdCount >= PROC_CD) {
					procCdCount -= PROC_CD;

					SyncedAttack.BOMB.initiateSyncedAttackSingle(state, p.getSchmuck(), p.getSchmuck().getPixelPosition(), new Vector2());
				}
				procCdCount += delta;
			}
		}.setUserOnly(true);
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) PROC_CD),
				String.valueOf((int) Bomb.BOMB_EXPLOSION_DAMAGE)};
	}
}
