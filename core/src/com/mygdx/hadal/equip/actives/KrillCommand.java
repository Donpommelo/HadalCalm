package com.mygdx.hadal.equip.actives;

import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.equip.ActiveItem;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;

/**
 * @author Mildsinger Motherford
 */
public class KrillCommand extends ActiveItem {

	private static final float MAX_CHARGE = 3.0f;

	public KrillCommand(Player user) {
		super(user, MAX_CHARGE);
	}

	@Override
	public void useItem(PlayState state, PlayerBodyData user) {
		SyncedAttack.KRILL_COMMAND.initiateSyncedAttackSingle(state, user.getPlayer(), user.getPlayer().getPixelPosition(), weaponVelo);
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) MAX_CHARGE)};
	}
}
