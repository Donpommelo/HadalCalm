package com.mygdx.hadal.equip.artifacts;

import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.equip.Equippable;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;

public class TenuousGripOnReality extends Artifact {

	private static final int SLOT_COST = 3;

	private static final float BONUS_INVIS = 1.5f;

	public TenuousGripOnReality() {
		super(SLOT_COST);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new Status(state, p) {

			@Override
			public void onReloadStart(Equippable tool) {
				if (tool.getClipLeft() == 0) {
					SyncedAttack.INVISIBILITY_ON.initiateSyncedAttackNoHbox(state, p.getPlayer(), p.getPlayer().getPixelPosition(),
							true, tool.getReloadTime() * BONUS_INVIS);
				}
			}
		};
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) (BONUS_INVIS * 100))};
	}
}
