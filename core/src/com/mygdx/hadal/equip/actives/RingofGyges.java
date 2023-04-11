package com.mygdx.hadal.equip.actives;

import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.equip.ActiveItem;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;

/**
 * @author Mobourne Manfosteen
 */
public class RingofGyges extends ActiveItem {

	private static final float MAX_CHARGE = 18.0f;
	
	private static final float DURATION = 8.0f;
	
	public RingofGyges(Player user) {
		super(user, MAX_CHARGE);
	}
	
	@Override
	public void useItem(PlayState state, PlayerBodyData user) {
		SyncedAttack.INVISIBILITY_ON.initiateSyncedAttackNoHbox(state, user.getPlayer(), user.getPlayer().getPixelPosition(),
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
