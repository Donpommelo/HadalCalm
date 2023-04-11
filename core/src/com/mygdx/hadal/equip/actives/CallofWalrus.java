package com.mygdx.hadal.equip.actives;

import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.battle.attacks.active.CallofWalrusUse;
import com.mygdx.hadal.equip.ActiveItem;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;

/**
 * @author Hedeon Holkner
 */
public class CallofWalrus extends ActiveItem {

	private static final float MAX_CHARGE = 12.0f;
	
	private static final float BUFF_DURATION = CallofWalrusUse.BUFF_DURATION;
	private static final float ATK_SPD_BUFF = CallofWalrusUse.ATK_SPD_BUFF;
	private static final float DAMAGE_BUFF = CallofWalrusUse.DAMAGE_BUFF;

	public CallofWalrus(Player user) {
		super(user, MAX_CHARGE);
	}
	
	@Override
	public void useItem(PlayState state, PlayerBodyData user) {
		SyncedAttack.CALL_OF_WALRUS.initiateSyncedAttackSingle(state, user.getPlayer(), user.getPlayer().getPixelPosition(), weaponVelo);
	}
	
	@Override
	public float getUseDuration() { return BUFF_DURATION; }

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) MAX_CHARGE),
				String.valueOf(BUFF_DURATION),
				String.valueOf((int) (ATK_SPD_BUFF * 100)),
				String.valueOf((int) (DAMAGE_BUFF * 100))};
	}
}
