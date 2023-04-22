package com.mygdx.hadal.equip.actives;

import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.equip.ActiveItem;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;

/**
 * @author Fibbadon Flabitha
 */
public class ForceofWill extends ActiveItem {

	private static final float MAX_CHARGE = 15.0f;
	
	private static final float DURATION = 2.0f;
	
	public ForceofWill(Player user) {
		super(user, MAX_CHARGE);
	}
	
	@Override
	public void useItem(PlayState state, PlayerBodyData user) {
		SyncedAttack.INVINCIBILITY.initiateSyncedAttackNoHbox(state, user.getPlayer(), user.getPlayer().getPixelPosition(),
				true, DURATION);
	}

	@Override
	public float getUseDuration() { return DURATION; }

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) MAX_CHARGE),
				String.valueOf((int) DURATION)};
	}
}
