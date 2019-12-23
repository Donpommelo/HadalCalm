package com.mygdx.hadal.actors;

import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.states.PlayState;

/**
 * This is a client version of the UIPlay.
 * The only different thing about this is that it reads its data from "override" fields of the player that are filled by info received from the server.
 * @author Zachary Tu
 *
 */
public class UIPlayClient extends UIPlay {

	public UIPlayClient(PlayState state, Player player) {
		super(state, player);
	}
	
	@Override
	public void calcVars() {
		//Calc the ratios needed to draw the bars
		hpRatio = player.getPlayerData().getCurrentHp() / player.getPlayerData().getOverrideMaxHp();
		hpMax = player.getPlayerData().getOverrideMaxHp();
		fuelRatio = player.getPlayerData().getCurrentFuel() / player.getPlayerData().getOverrideMaxFuel();
		fuelCutoffRatio = player.getPlayerData().getOverrideAirblastCost() / player.getPlayerData().getOverrideMaxFuel();
		weaponText = player.getPlayerData().getOverrideClipLeft() + "/" + player.getPlayerData().getOverrideClipSize();
		ammoText = player.getPlayerData().getOverrideAmmoSize() + "";
	}
}
