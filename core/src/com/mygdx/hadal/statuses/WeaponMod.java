package com.mygdx.hadal.statuses;

import com.mygdx.hadal.equip.Equipable;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;

public class WeaponMod extends Status {

	
	private Equipable moddedTool;
	private Status mod;
	
	public WeaponMod(PlayState state, BodyData p, BodyData v, int pr, Equipable tool, Status mod){
		super(state, 0, "", true, false, false, false, p, v, pr);
		this.moddedTool = tool;
		this.mod = mod;
	}
	
	public float statusProcTime(int procTime, BodyData schmuck, float amount, Status status, Equipable tool, DamageTypes... tags) {

		if (tool.equals(moddedTool)) {
			return mod.statusProcTime(procTime, schmuck, amount, status, tool, tags);
		} else {
			return amount;
		}
	}
}
