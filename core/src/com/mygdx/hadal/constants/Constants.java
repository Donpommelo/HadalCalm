package com.mygdx.hadal.constants;

import com.badlogic.gdx.math.Interpolation;

/**
 * Constants used throughout the game.
 * @author Layonnaise Lelazar
 */
public class Constants {
	
	//Pixels per Meter. Transitioning between Box2d coordinates and libgdx ones.
	public static final float PPM = 32;

	//interval that certain forces are applied to avoid being affected by framerate
    public static final float INTERVAL = 1 / 60.0f;

    //interval that we process world physics
    public static final float PHYSICS_TIME = 1 / 200.0f;

    //duration of flashes for several flashing entities
    public static final float FLASH = 0.1f;

    //Synced Player Properties
    public static final short GROUNDED = 1;
    public static final short JUMPING = 2;
    public static final short RUNNING = 4;
    public static final short HOVERING = 8;
    public static final short RELOADING = 16;
    public static final short SHOOTING = 32;
    public static final short INVISIBLE = 64;
    public static final short TRANSPARENT = 128;
    public static final short TRANSLUCENT = 256;
    public static final short TYPING = 512;

    //misc constants
    public static final int MAX_NAME_LENGTH = 25;
    public static final int MAX_NAME_LENGTH_SHORT = 20;
    public static final int MAX_NAME_LENGTH_SUPER_SHORT = 15;
    public static final int MAX_NAME_LENGTH_LONG = 30;
    public static final int MAX_NAME_LENGTH_TOTAL = 40;
    public static final int MAX_MESSAGE_LENGTH = 80;

    public static final float TRANSITION_DURATION = 0.25f;
    public static final float TRANSITION_DURATION_SLOW = 0.5f;
    public static final Interpolation INTP_FASTSLOW = Interpolation.fastSlow;

    //pickup types
    public static final int PICKUP_HEALTH = 0;
    public static final int PICKUP_FUEL = 1;
    public static final int PICKUP_AMMO = 2;
}
