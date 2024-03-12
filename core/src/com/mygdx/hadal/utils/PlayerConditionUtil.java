package com.mygdx.hadal.utils;

import com.mygdx.hadal.constants.Constants;

/**
 * "Conditions" are a series of booleans that describe the actions the player is currently engaged in (running, jumping, etc).
 * This util contains functions that translate these conditions between different data types.
 * This is used to send this information between server and client to save data
 * @author Joastein Jebiscus
 */
public class PlayerConditionUtil {

    /**
     * This takes in a list of boolean flags relevant to the player's condition and returns a short that encapsulates all
     * those values and can be unscrambled by the recipient of the packet.
     */
    public static short conditionToCode(boolean grounded, boolean jumping, boolean running, boolean hovering, boolean invisible,
                                        boolean translucent, boolean tranparent, boolean reloading, boolean shooting, boolean typing) {

        return (short) ((grounded ? Constants.GROUNDED : 0) |
                (jumping ? Constants.JUMPING : 0) |
                (running ? Constants.RUNNING : 0) |
                (hovering ? Constants.HOVERING : 0) |
                (reloading ? Constants.RELOADING : 0) |
                (shooting ? Constants.SHOOTING : 0) |
                (invisible ? Constants.INVISIBLE : 0) |
                (translucent ? Constants.TRANSLUCENT : 0) |
                (tranparent ? Constants.TRANSPARENT : 0) |
                (typing ? Constants.TYPING : 0));
    }

    /**
     * The reverse of the above function. This receives a short condition code and returns whether a specific condition
     * is true or not (i.e. is the player walking/jumping etc)
     */
    public static boolean codeToCondition(short code, short condition) { return code == (code | condition); }
}
