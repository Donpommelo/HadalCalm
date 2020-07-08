package com.mygdx.hadal.statuses;

/**
 * This is a list of possible damage tags. These are checked when damage is inflicted/healed for specific effects to check.
 * These are also used for custom kill messages for specific weapons
 * @author Zachary Tu
 */
public enum DamageTypes {
	DEFLECT,
	REFLECT,
	
	MELEE,
	RANGED,
	
	CUTTING,
	POKING,
	WHACKING,
	
	BEES,
	BULLET,
	ENERGY,
	EXPLOSIVE,
	FIRE,
	MAGIC,
	POISON,
	SHRAPNEL,
	SOUND,
	WATER,
	
	COLA,
	SLODGE,
	SNIPE,
	TRICK,
	STUTTER,
	
	LIVES_OUT,
	BLASTZONE,
	DISCONNECT,
	
	REGEN,
	MEDPAK,
	LIFESTEAL,
}
