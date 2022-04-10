package com.mygdx.hadal.battle;

/**
 * This is a list of possible damage tags. These are checked when damage is inflicted/healed for specific effects to check.
 * These are also used for custom kill messages for specific weapons
 * @author Skankykong Storabeau
 */
public enum DamageTag {
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
	LIGHTNING,
	MAGIC,
	POISON,
	SHRAPNEL,
	SOUND,
	WATER,
	
	REGEN,
	MEDPAK,
	LIFESTEAL,
}
