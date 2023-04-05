package com.mygdx.hadal.equip.actives;

import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.battle.attacks.active.JumpKickProjectile;
import com.mygdx.hadal.equip.ActiveItem;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;

/**
 * @author Krabitha Krenitzel
 */
public class JumpKick extends ActiveItem {

	private static final float MAX_CHARGE = 8.0f;
	private static final float BASE_DAMAGE = JumpKickProjectile.BASE_DAMAGE;

	public JumpKick(Player user) {
		super(user, MAX_CHARGE);
	}
	
	@Override
	public void useItem(PlayState state, PlayerBodyData user) {
		SyncedAttack.JUMP_KICK.initiateSyncedAttackSingle(state, user.getPlayer(), user.getPlayer().getPixelPosition(), weaponVelo);
	}

	@Override
	public float getBotRangeMin() { return 15.0f; }

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) MAX_CHARGE),
				String.valueOf((int) BASE_DAMAGE)};
	}
}
