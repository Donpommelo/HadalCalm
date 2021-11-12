package com.mygdx.hadal.statuses;

import com.mygdx.hadal.equip.WeaponUtils;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.text.HText;
import com.mygdx.hadal.utils.Stats;

import static com.mygdx.hadal.utils.Constants.MAX_NAME_LENGTH;

/**
 * This status is inflicted upon units that pick up the flag. It is a debuff intended to make capturing flags harder
 * @author Luggozzerella Lopants
 */
public class CarryingFlag extends Status {

	//this is the magnitude of the slow.
	private static final float fuelRegen = 4.0f;

	public CarryingFlag(PlayState state, BodyData i) {
		super(state, i);
	}

	@Override
	public void statChanges() {
		inflicted.setStat(Stats.FUEL_REGEN, fuelRegen);
	}

	@Override
	public void onDeath(BodyData perp) {
		if (perp instanceof PlayerBodyData playerData) {
			String playerName = WeaponUtils.getPlayerColorName(playerData.getPlayer(), MAX_NAME_LENGTH);
			state.getKillFeed().addNotification(HText.CTF_DEFENDED.text(playerName), true);
		}
	}

	public statusStackType getStackType() {
		return statusStackType.REPLACE;
	}
}
