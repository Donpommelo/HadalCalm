package com.mygdx.hadal.actors;

import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.Stats;

/**
 * This is a client version of the UIPlay.
 * The only different thing about this is that it reads its data from "override" fields of the player that are filled by info received from the server.
 * @author Liggnut Leblatt
 */
public class UIPlayClient extends UIPlay {

	//Override stats are used by the client to display in the ui instead of actually having the real server stats.
	private int overrideClipLeft;
	private int overrideAmmoSize;
	private float overrideActivePercent;

	public UIPlayClient(PlayState state) {
		super(state);
	}
	
	@Override
	public void calcVars() {

		//Calc the fields needed to draw the bars. This mostly accounts for fields not synced for the client
		if (state.getPlayer().getPlayerData() != null) {
			hpRatio = state.getPlayer().getPlayerData().getCurrentHp() / state.getPlayer().getPlayerData().getStat(Stats.MAX_HP);
			hpMax = state.getPlayer().getPlayerData().getStat(Stats.MAX_HP);
			fuelRatio = state.getPlayer().getPlayerData().getCurrentFuel() / state.getPlayer().getPlayerData().getStat(Stats.MAX_FUEL);
			fuelCutoffRatio = state.getPlayer().getPlayerData().getAirblastCost() / state.getPlayer().getPlayerData().getStat(Stats.MAX_FUEL);
			numWeaponSlots = state.getPlayer().getPlayerData().getNumWeaponSlots();

			if (state.getPlayer().getPlayerData().getCurrentTool() instanceof RangedWeapon ranged) {
				weaponText = overrideClipLeft + "/" + ranged.getClipSize();
				ammoText = overrideAmmoSize + "";
			} else {
				weaponText = "";
				ammoText = "";
			}
		}

		//Calc the ratios needed to draw the bars
		activePercent = overrideActivePercent;
		
		if (bossFight && boss.getBody() != null) {
			bossHpRatio = boss.getBodyData().getCurrentHp() / boss.getBodyData().getStat(Stats.MAX_HP);
			bossHpRatio = bossHpFloor + (bossHpRatio * (1 - bossHpFloor));
		}
	}
	
	public void setOverrideClipLeft(int overrideClipLeft) { this.overrideClipLeft = overrideClipLeft; }
	
	public void setOverrideAmmoSize(int overrideAmmoSize) {	this.overrideAmmoSize = overrideAmmoSize; }

	public void setOverrideActivePercent(float overrideActivePercent) { this.overrideActivePercent = overrideActivePercent; }
}
