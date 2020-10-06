package com.mygdx.hadal.utils;

import com.badlogic.gdx.math.Vector2;

/**
 * This contains a couple of utils used for converting angles and vectors
 * @author Zachary Tu
 */
public final class SteeringUtil {
	
	public static float vectorToAngle(Vector2 vector) {
		return (float) Math.atan2(-vector.x, vector.y);
	}
}
