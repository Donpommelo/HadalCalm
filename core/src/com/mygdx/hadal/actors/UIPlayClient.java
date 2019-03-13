package com.mygdx.hadal.actors;

import com.badlogic.gdx.assets.AssetManager;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.states.PlayState;

public class UIPlayClient extends UIPlay {

	public UIPlayClient(AssetManager assetManager, PlayState state, Player player) {
		super(assetManager, state, player);
	}
	
	@Override
	public void calcVars() {
		//Calc the ratios needed to draw the bars
		hpRatio = player.getPlayerData().getCurrentHp() / player.getPlayerData().getOverrideMaxHp();
		fuelRatio = player.getPlayerData().getCurrentFuel() / player.getPlayerData().getOverrideMaxFuel();
		fuelCutoffRatio = player.getPlayerData().getOverrideAirblastCost() / player.getPlayerData().getOverrideMaxFuel();
		weaponText = player.getPlayerData().getCurrentTool().getTextClient(player.getPlayerData().getOverrideClipSize());
	}
}
