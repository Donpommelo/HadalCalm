package com.mygdx.hadal.equip.actives;

import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.battle.attacks.active.HydraulicUppercutProjectile;
import com.mygdx.hadal.equip.ActiveItem;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;

/**
 * @author Pronghort Phunzales
 */
public class HydraulicUppercut extends ActiveItem {

	private static final float MAX_CHARGE = 8.0f;
	private static final float BASE_DAMAGE = HydraulicUppercutProjectile.BASE_DAMAGE;
	private static final float LIFESPAN = HydraulicUppercutProjectile.LIFESPAN;

	public HydraulicUppercut(Player user) { super(user, MAX_CHARGE); }
	
	@Override
	public void useItem(PlayState state, PlayerBodyData user) {
		SyncedAttack.HYDRAUlIC_UPPERCUT.initiateSyncedAttackSingle(state, user.getPlayer(), user.getPlayer().getPixelPosition(), weaponVelo);
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) MAX_CHARGE),
				String.valueOf((int) BASE_DAMAGE),
				String.valueOf(LIFESPAN)};
	}
}
