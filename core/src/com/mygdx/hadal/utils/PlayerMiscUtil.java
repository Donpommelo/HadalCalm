package com.mygdx.hadal.utils;

import com.badlogic.gdx.math.Vector2;

/**
 * This util contains some misc functions used by the player that didn't really go anywhere else.
 * Tentatively, this only includes the freeze frame function for handling animations
 *
 */
public class PlayerMiscUtil {

    /**
     * When the player is in the air, their animation freezes. This gets the frame for that
     * @param velocity: player's linear velocity
     * @param reverse: which direction is the player facing
     * @return the integer frame number that should be displayed given the player's movement status
     */
    public static int getFreezeFrame(Vector2 velocity, boolean reverse) {
        if (Math.abs(velocity.x) > Math.abs(velocity.y)) {
            return reverse ? 5 : 2;
        } else {
            return reverse ? 1 : 6;
        }
    }
}
