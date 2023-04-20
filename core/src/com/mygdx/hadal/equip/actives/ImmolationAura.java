package com.mygdx.hadal.equip.actives;

import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.battle.attacks.active.Immolation;
import com.mygdx.hadal.equip.ActiveItem;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;

/**
 * @author Suckette Smothro
 */
public class ImmolationAura extends ActiveItem {

	private static final float MAX_CHARGE = 13.0f;

	private static final float BASE_DAMAGE = Immolation.BASE_DAMAGE;
	private static final float LIFESPAN = Immolation.LIFESPAN;
	private static final float BURN_INTERVAL = Immolation.BURN_INTERVAL;

	public ImmolationAura(Player user) {
		super(user, MAX_CHARGE);
	}
	
	@Override
	public void useItem(PlayState state, PlayerBodyData user) {
		SyncedAttack.IMMOLATION.initiateSyncedAttackNoHbox(state, user.getPlayer(), user.getPlayer().getPixelPosition(), true);
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) MAX_CHARGE),
				String.valueOf((int) (BASE_DAMAGE / BURN_INTERVAL)),
				String.valueOf((int) LIFESPAN)};
	}
}
