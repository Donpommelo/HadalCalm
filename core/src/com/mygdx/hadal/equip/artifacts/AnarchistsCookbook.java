package com.mygdx.hadal.equip.artifacts;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;

import static com.mygdx.hadal.battle.WeaponUtils.BOMB_EXPLOSION_DAMAGE;

public class AnarchistsCookbook extends Artifact {

	private static final int slotCost = 1;
	
	private static final float procCd = 3.0f;
	
	public AnarchistsCookbook() {
		super(slotCost);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment= new Status(state, p) {
			
			private float procCdCount;
			@Override
			public void timePassing(float delta) {
				if (procCdCount >= procCd) {
					procCdCount -= procCd;
					SyncedAttack.BOMB.initiateSyncedAttackSingle(state, p.getSchmuck(), p.getSchmuck().getPixelPosition(), new Vector2());
				}
				procCdCount += delta;
			}
		};
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) procCd),
				String.valueOf((int) BOMB_EXPLOSION_DAMAGE)};
	}
}
