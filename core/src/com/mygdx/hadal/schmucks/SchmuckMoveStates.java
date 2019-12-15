package com.mygdx.hadal.schmucks;

/**
 * A List of movement states for all schmucks; player or enemy
 * @author Zachary Tu
 *
 */
public enum SchmuckMoveStates {
	STAND,
	MOVE_LEFT,
	MOVE_RIGHT,
	
	FISH_ROAMING,
	FISH_CHASING,
	
	TURRET_SHOOTING,
	TURRET_NOTSHOOTING,
	
	BOSS_WAITING,
	BOSS_ATTACKING
}
