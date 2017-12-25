package com.mygdx.hadal.utils;

/**
 * Constants used throughout the game.
 * @author Zachary Tu
 *
 */
public class Constants {
	
	//Pixels per Meter. Transitioning between Box2d coordinates and libgdx ones.
	public static final float PPM = 32;
	
	// Filters
    public static final short BIT_WALL = 1;
    public static final short BIT_PLAYER = 2;
    public static final short BIT_SENSOR = 4;
    public static final short BIT_PROJECTILE = 8;
    public static final short BIT_ENEMY = 16;

    public static final short PLAYER_HITBOX = -1;
    public static final short ENEMY_HITBOX = -2;
    
}
