package com.mygdx.hadal.utils;

import com.badlogic.gdx.math.Vector2;

/**
 * This contains helper functions regarding the game world
 */
public class WorldUtil {

    /**
     * This is a check done before any raycast
     * We check that the points are valid and not equal to one another
     */
    public static boolean preRaycastCheck(Vector2 point1, Vector2 point2) {
        return Float.isFinite(point1.x) && Float.isFinite(point1.y) && Float.isFinite(point2.x) && Float.isFinite(point2.y)
                && (point1.x != point2.x || point1.y != point2.y);
    }
}
