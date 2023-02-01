package com.mygdx.hadal.equip.actives;

import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.equip.ActiveItem;
import com.mygdx.hadal.event.PickupEquip;
import com.mygdx.hadal.save.UnlockEquip;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;

/**
 * @author Preewenhoek Phistein
 */
public class SupplyDrop extends ActiveItem {

	private static final float MAX_CHARGE = 10.0f;

	private static final float EQUIP_DROP_LIFEPAN = 10.0f;

	public SupplyDrop(Player user) {
		super(user, MAX_CHARGE);
		setCurrentCharge(MAX_CHARGE);
	}
	
	@Override
	public void useItem(PlayState state, PlayerBodyData user) {
		SoundEffect.MAGIC1_ACTIVE.playUniversal(state, user.getPlayer().getPixelPosition(), 1.0f, false);
		new PickupEquip(state, user.getPlayer().getPixelPosition(), UnlockEquip.getRandWeapFromPool(state, ""), EQUIP_DROP_LIFEPAN);
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) MAX_CHARGE)};
	}
}
