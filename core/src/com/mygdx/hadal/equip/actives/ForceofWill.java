package com.mygdx.hadal.equip.actives;

import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.battle.attacks.active.ForceInvulnerability;
import com.mygdx.hadal.equip.ActiveItem;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;

/**
 * @author Fibbadon Flabitha
 */
public class ForceofWill extends ActiveItem {

	private static final float MAX_CHARGE = 15.0f;
	
	private static final float DURATION = ForceInvulnerability.DURATION;
	
	public ForceofWill(Player user) {
		super(user, MAX_CHARGE);
	}
	
	@Override
	public void useItem(PlayState state, PlayerBodyData user) {
		SyncedAttack.FORCE_OF_WILL.initiateSyncedAttackNoHbox(state, user.getPlayer(), user.getPlayer().getPixelPosition(), true);
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
