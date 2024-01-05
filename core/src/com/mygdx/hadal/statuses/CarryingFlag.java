package com.mygdx.hadal.statuses;

import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.text.UIText;
import com.mygdx.hadal.constants.Stats;
import com.mygdx.hadal.utils.TextUtil;

import static com.mygdx.hadal.constants.Constants.MAX_NAME_LENGTH;

/**
 * This status is inflicted upon units that pick up the flag. It is a debuff intended to make capturing flags harder
 * @author Luggozzerella Lopants
 */
public class CarryingFlag extends Status {

	//this is the amount of fuel regen when carrying the flag.
	private static final float FUEL_REGEN = 8.0f;

	public CarryingFlag(PlayState state, BodyData i) {
		super(state, i);
		this.setServerOnly(true);
	}

	@Override
	public void statChanges() {
		inflicted.setStat(Stats.FUEL_REGEN, FUEL_REGEN);
	}

	@Override
	public void onDeath(BodyData perp, DamageSource source) {

		//on death, notify players that flag was dropped
		if (perp instanceof PlayerBodyData playerData) {
			String playerName = TextUtil.getPlayerColorName(playerData.getPlayer(), MAX_NAME_LENGTH);
			state.getKillFeed().addNotification(UIText.CTF_DEFENDED.text(playerName), true);
		}
	}

	public statusStackType getStackType() {
		return statusStackType.REPLACE;
	}
}
