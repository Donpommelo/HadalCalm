package com.mygdx.hadal.statuses;

import com.mygdx.hadal.equip.Equipable;
import com.mygdx.hadal.equip.mods.WeaponMod;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;

public class WeaponModifier extends Status {
	
	private Equipable moddedTool;
	private WeaponMod constantMod;
	private Status mod;
	
	public WeaponModifier(PlayState state, String name, String descr, BodyData i, Equipable tool, 
			WeaponMod constantMod, Status mod){
		super(state, name, descr, false, i);
		this.moddedTool = tool;
		this.constantMod = constantMod;
		this.mod = mod;
	}
	
	@Override
	public float statusProcTime(int procTime, BodyData schmuck, float amount, Status status, Equipable tool, Hitbox hbox, DamageTypes... tags) {

		if (tool != null) {
			if (tool.equals(moddedTool)) {
				return mod.statusProcTime(procTime, schmuck, amount, status, tool, hbox, tags);
			}
		}
		return amount;
	}

	public WeaponMod getConstantMod() {
		return constantMod;
	}	
}
