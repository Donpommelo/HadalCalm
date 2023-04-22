package com.mygdx.hadal.equip.actives;

import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.battle.attacks.active.DepthChargeUse;
import com.mygdx.hadal.equip.ActiveItem;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;

/**
 * @author Muburough Mapodilla
 */
public class DepthCharge extends ActiveItem {

	private static final float MAX_CHARGE = 10.0f;
	
	private static final float DURATION = DepthChargeUse.DURATION;
	private static final float PROC_CD = DepthChargeUse.PROC_CD;
	private static final float EXPLOSION_DAMAGE = DepthChargeUse.EXPLOSION_DAMAGE;

	public DepthCharge(Player user) {
		super(user, MAX_CHARGE);
	}
	
	@Override
	public void useItem(PlayState state, PlayerBodyData user) {
		SyncedAttack.DEPTH_CHARGE.initiateSyncedAttackNoHbox(state, user.getPlayer(), user.getPlayer().getPixelPosition(), true);
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) MAX_CHARGE),
				String.valueOf((int) (DURATION / PROC_CD)),
				String.valueOf((int) EXPLOSION_DAMAGE)};
	}
}
