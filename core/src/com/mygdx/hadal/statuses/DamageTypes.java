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
	COLA,
	ENERGY,
	EXPLOSIVE,
	FIRE,
	FISH,
	LIGHTNING,
	MAGIC,
	PARTY,
	POISON,
	SHRAPNEL,
	SLODGE,
	SNIPE,
	SOUND,
	STUTTER,
	TRICK,
	WATER,
	
	LIVES_OUT,
	BLASTZONE,
	DISCONNECT,
	
	REGEN,
	MEDPAK,
	LIFESTEAL,
}
