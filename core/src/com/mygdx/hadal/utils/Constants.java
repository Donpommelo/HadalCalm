package com.mygdx.hadal.utils;

import com.badlogic.gdx.math.Interpolation;

/**
 * Constants used throughout the game.
 * @author Layonnaise Lelazar
 */
public class Constants {
	
	//Pixels per Meter. Transitioning between Box2d coordinates and libgdx ones.
	public static final float PPM = 32;
	
	//Body passability types
    public static final short BIT_WALL = 1;
    public static final short BIT_PLAYER = 2;
    public static final short BIT_SENSOR = 4;
    public static final short BIT_PROJECTILE = 8;
    public static final short BIT_ENEMY = 16;
    public static final short BIT_DROPTHROUGHWALL = 32;

    //Hitbox Filters
    public static final short PLAYER_HITBOX = -1;
    public static final short ENEMY_HITBOX = -2;

    //misc constants
    public static final int MAX_NAME_LENGTH = 25;
    public static final int MAX_NAME_LENGTH_SHORT = 20;
    public static final int MAX_NAME_LENGTH_LONG = 30;
    public static final int MAX_NAME_LENGTH_TOTAL = 40;
    public static final int MAX_MESSAGE_LENGTH = 80;

    public static final float TRANSITION_DURATION = 0.25f;
    public static final float TRANSITION_DURATION_SLOW = 0.5f;
    public static final Interpolation INTP_FASTSLOW = Interpolation.fastSlow;
}
