package com.mygdx.hadal.equip.actives;

import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.battle.attacks.active.PlusMinusUse;
import com.mygdx.hadal.equip.ActiveItem;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;

/**
 * @author Borpwood Bluwood
 */
public class PlusMinus extends ActiveItem {

	private static final float MAX_CHARGE = 12.0f;

	private static final float DURATION = PlusMinusUse.DURATION;
	private static final float PROC_CD = PlusMinusUse.PROC_CD;
	private static final float CHAIN_DAMAGE = PlusMinusUse.CHAIN_DAMAGE;
	private static final int CHAIN_AMOUNT = PlusMinusUse.CHAIN_AMOUNT;
	
	public PlusMinus(Player user) {
		super(user, MAX_CHARGE);
	}
	
	@Override
	public void useItem(PlayState state, PlayerBodyData user) {
		SyncedAttack.PLUS_MINUS.initiateSyncedAttackNoHbox(state, user.getPlayer(), user.getPlayer().getPixelPosition(), true);
	}

	@Override
	public float getBotRangeMin() { return 7.0f; }

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) MAX_CHARGE),
				String.valueOf((int) (DURATION / PROC_CD)),
				String.valueOf((int) CHAIN_DAMAGE),
				String.valueOf(CHAIN_AMOUNT)};
	}
}
