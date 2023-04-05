package com.mygdx.hadal.equip.actives;

import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.equip.ActiveItem;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;

/**
 * @author Preewenhoek Phistein
 */
public class SupplyDrop extends ActiveItem {

	private static final float MAX_CHARGE = 10.0f;

	public SupplyDrop(Player user) {
		super(user, MAX_CHARGE);
		setCurrentCharge(MAX_CHARGE);
	}
	
	@Override
	public void useItem(PlayState state, PlayerBodyData user) {
		SyncedAttack.SUPPLY_DROP.initiateSyncedAttackNoHbox(state, user.getPlayer(), user.getPlayer().getPixelPosition(), false);
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) MAX_CHARGE)};
	}
}
