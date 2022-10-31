package com.mygdx.hadal.constants;

import com.badlogic.gdx.math.Interpolation;

/**
 * Constants used throughout the game.
 * @author Layonnaise Lelazar
 */
public class Constants {
	
	//Pixels per Meter. Transitioning between Box2d coordinates and libgdx ones.
	public static final float PPM = 32;

	//interval that many physics are applied to avoid being affected by framerate
	public static final float INTERVAL = 1 / 60.0f;

    //duration of flashes for several flashing entities
    public static final float FLASH = 0.1f;

	//Body passability types
    public static final short BIT_WALL = 1;
    public static final short BIT_PLAYER = 2;
    public static final short BIT_SENSOR = 4;
    public static final short BIT_PROJECTILE = 8;
    public static final short BIT_ENEMY = 16;
    public static final short BIT_DROPTHROUGHWALL = 32;
    public static final short BIT_PICKUP_RADIUS = 64;

    //Hitbox Filters
    public static final short PLAYER_HITBOX = -1;
    public static final short ENEMY_HITBOX = -2;

    //Synced Player Properties
    public static final short GROUNDED = 1;
    public static final short RUNNING = 2;
    public static final short HOVERING = 4;
    public static final short RELOADING = 8;
    public static final short INVISIBLE = 16;
    public static final short TRANSPARENT = 32;
    public static final short TRANSLUCENT = 64;

    //misc constants
    public static final int MAX_NAME_LENGTH = 25;
    public static final int MAX_NAME_LENGTH_SHORT = 20;
    public static final int MAX_NAME_LENGTH_LONG = 30;
    public static final int MAX_NAME_LENGTH_TOTAL = 40;
    public static final int MAX_MESSAGE_LENGTH = 80;

    public static final float TRANSITION_DURATION = 0.25f;
    public static final float TRANSITION_DURATION_SLOW = 0.5f;
    public static final Interpolation INTP_FASTSLOW = Interpolation.fastSlow;

    //status priorities
    public static final int PRIORITY_DEFAULT = 0;
    public static final int PRIORITY_SET = 6;
    public static final int PRIORITY_PROC = 7;
    public static final int PRIORITY_MULT_SCALE= 8;
    public static final int PRIORITY_SCALE = 9;
    public static final int PRIORITY_LAST = 10;
    public static final int PRIORITY_LAST_LAST = 11;

    //pickup types
    public static final int PICKUP_HEALTH = 0;
    public static final int PICKUP_FUEL = 1;
    public static final int PICKUP_AMMO = 2;
}
