package com.mygdx.hadal.equip.actives;

import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.battle.attacks.active.Terraform;
import com.mygdx.hadal.equip.ActiveItem;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;

/**
 * @author Nuberry Nolpgins
 */
public class Terraformer extends ActiveItem {

	private static final float MAX_CHARGE = 8.0f;
	
	private static final int BLOCK_HP = Terraform.BLOCK_HP;

	public Terraformer(Player user) {
		super(user, MAX_CHARGE);
	}
	
	@Override
	public void useItem(PlayState state, PlayerBodyData user) {
		SyncedAttack.TERRAFORM.initiateSyncedAttackNoHbox(state, user.getPlayer(), user.getPlayer().getPixelPosition(),
				false, weaponVelo.x, weaponVelo.y);
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) MAX_CHARGE),
				String.valueOf(BLOCK_HP)};
	}
}
