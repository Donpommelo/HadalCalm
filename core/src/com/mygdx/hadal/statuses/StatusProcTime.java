package com.mygdx.hadal.statuses;

/**
 * These represent all of the parts of the game where a status can activate
 * @author Zachary Tu
 *
 */
public enum StatusProcTime {
	ON_INFLICT,
	ON_REMOVE,
	STAT_CHANGE,
	DEAL_DAMAGE,
	RECEIVE_DAMAGE,
	TIME_PASS,
	ON_KILL,
	ON_DEATH,
	ON_HEAL,
	WHILE_ATTACKING,
	ON_SHOOT,
	WHILE_RELOADING,
	ON_RELOAD,
	HITBOX_CREATION,
	LEVEL_START,
	ON_AIRBLAST,
}
