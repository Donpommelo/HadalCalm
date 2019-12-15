package com.mygdx.hadal.statuses;

import com.mygdx.hadal.equip.Equipable;
import com.mygdx.hadal.equip.mods.WeaponMod;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;

/**
 * A Weapon Modifier is a status that is only active when a player has a specific weapon equipped.
 * @author Zachary Tu
 *
 */
public class WeaponModifier extends Status {
	
	//The status will be active when this weapon is active
	private Equipable moddedTool;
	
	//A Weapon mod that will be applied when thw above weapon is active
	private WeaponMod constantMod;
	
	//A Status that will activate if the above weapon is active. This corresponds to the WeaponMod
	private Status mod;
	
	public WeaponModifier(PlayState state, String name, String descr, BodyData i, Equipable tool, 
			WeaponMod constantMod, Status mod){
		super(state, name, descr, i);
		this.moddedTool = tool;
		this.constantMod = constantMod;
		this.mod = mod;
	}
	
	@Override
	public float statusProcTime(StatusProcTime procTime, BodyData schmuck, float amount, Status status, Equipable tool, Hitbox hbox, DamageTypes... tags) {

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
