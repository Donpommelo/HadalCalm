package com.mygdx.hadal.equip.actives;

import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.battle.attacks.active.SpringLoaderUse;
import com.mygdx.hadal.equip.ActiveItem;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;

/**
 * @author Wrudaus Wibanfoo
 */
public class SpringLoader extends ActiveItem {

	private static final float MAX_CHARGE = 3.0f;
	
	private static final float SPRING_DURATION = SpringLoaderUse.SPRING_DURATION;
	
	public SpringLoader(Player user) {
		super(user, MAX_CHARGE);
	}
	
	@Override
	public void useItem(PlayState state, PlayerBodyData user) {
		SyncedAttack.SPRING.initiateSyncedAttackNoHbox(state, user.getPlayer(), user.getPlayer().getMouseHelper().getPixelPosition(),
				true);
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) MAX_CHARGE),
				String.valueOf((int) SPRING_DURATION)};
	}
}
