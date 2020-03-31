package com.mygdx.hadal.actors;

import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.states.PlayState;

/**
 * This is a client version of the UIPlay.
 * The only different thing about this is that it reads its data from "override" fields of the player that are filled by info received from the server.
 * @author Zachary Tu
 *
 */
public class UIPlayClient extends UIPlay {

	//Override stats are used by the client to display in the ui instead of actually having the real server stats.
	private float overrideFuelPercent;
	private float overrideMaxHp;
	private float overrideMaxFuel;
	private float overrideAirblastCost;
	private int overrideClipSize;
	private int overrideClipLeft;
	private int overrideAmmoSize;
	private int overrideWeaponSlots;
	private int overrideArtifactSlots;
	private float overrideActivePercent;
	private float overrideBossHpPercent;
	private float overrideHealthVisibility;
	
	public UIPlayClient(PlayState state, Player player) {
		super(state, player);
	}
	
	@Override
	public void calcVars() {
		
		//Calc the ratios needed to draw the bars
		hpRatio = player.getPlayerData().getOverrideHpPercent();
		hpMax = overrideMaxHp;
		fuelRatio = overrideFuelPercent;
		fuelCutoffRatio = overrideAirblastCost / overrideMaxFuel;
		numWeaponSlots = overrideWeaponSlots;
		activePercent = overrideActivePercent;
		
		if (bossFight && boss.getBody() != null) {
			bossHpRatio = overrideBossHpPercent;
		}
		
		if (player.getPlayerData().getCurrentTool() instanceof RangedWeapon) {
			weaponText = overrideClipLeft + "/" + overrideClipSize;
			ammoText = overrideAmmoSize + "";
		} else {
			weaponText = "";
			ammoText = "";
		}
	}
	
	public float getOverrideMaxHp() { return overrideMaxHp;	}
	
	public void setOverrideFuelPercent(float overrideFuelPercent) {	this.overrideFuelPercent = overrideFuelPercent; }
	
	public void setOverrideClipLeft(int overrideClipLeft) { this.overrideClipLeft = overrideClipLeft; }
	
	public void setOverrideAmmoSize(int overrideAmmoSize) {	this.overrideAmmoSize = overrideAmmoSize; }
	
	public void setOverrideMaxHp(float overrideMaxHp) {	this.overrideMaxHp = overrideMaxHp;	}

	public void setOverrideMaxFuel(float overrideMaxFuel) {	this.overrideMaxFuel = overrideMaxFuel;	}

	public void setOverrideAirblastCost(float overrideAirblastCost) { this.overrideAirblastCost = overrideAirblastCost; }

	public void setOverrideClipSize(int overrideClipSize) {	this.overrideClipSize = overrideClipSize; }

	public void setOverrideWeaponSlots(int overrideWeaponSlots) { this.overrideWeaponSlots = overrideWeaponSlots; }
	
	public void setOverrideActivePercent(float overrideActivePercent) { this.overrideActivePercent = overrideActivePercent; }
	
	public void setOverrideBossHpPercent(float overrideBossHpPercent) {	this.overrideBossHpPercent = overrideBossHpPercent; }
	
	public void setOverrideArtifactSlots(int overrideArtifactSlots) { this.overrideArtifactSlots = overrideArtifactSlots; }
	
	public void setOverrideHealthVisibility(float overrideHealthVisibility) { this.overrideHealthVisibility = overrideHealthVisibility; }
	
	public int getOverrideArtifactSlots() { return this.overrideArtifactSlots; }
	
	public int getOverrideWeaponSlots() { return this.overrideWeaponSlots; }

	public float getHealthVisibility() { return this.overrideHealthVisibility; }

}
