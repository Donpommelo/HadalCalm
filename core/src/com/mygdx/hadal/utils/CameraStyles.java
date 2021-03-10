package com.mygdx.hadal.utils;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

/**
 * These utils describe various tools for moving the camera.
 */
public class CameraStyles {

    private final static Vector3 position = new Vector3();
    public static void lockOnTarget(Camera camera, Vector2 target) {
        position.set(camera.position);
        position.x = target.x;
        position.y = target.y;
        camera.position.set(position);
        camera.update();
    }

    public static void lerpToTarget(Camera camera, Vector2 target, float interpolation) {
        // a + (b - a) * lerp factor
        position.set(camera.position);
        position.x = (int) (camera.position.x + (target.x - camera.position.x) * interpolation);
        position.y = (int) (camera.position.y + (target.y - camera.position.y) * interpolation);
        camera.position.set(position);
        camera.update();
    }

    public static void lockAverageBetweenTargets(Camera camera, Vector2 targetA, Vector2 targetB) {
        position.set(camera.position);
        position.x = (targetA.x + targetB.x) / 2;
        position.y = (targetA.y + targetB.y) / 2;
        camera.position.set(position);
        camera.update();
    }

    public static void lerpAverageBetweenTargets(Camera camera, Vector2 targetA, Vector2 targetB) {
        float avgX = (targetA.x + targetB.x) / 2;
        float avgY = (targetA.y + targetB.y) / 2;

        position.set(camera.position);
        position.x = camera.position.x + (avgX - camera.position.x) * 0.1f;
        position.y = camera.position.y + (avgY - camera.position.y) * 0.1f;
        camera.position.set(position);
        camera.update();
    }

    public static boolean searchFocalPoints(OrthographicCamera camera, Array<Vector2> focalPoints, Vector2 target,
                                            float threshold, float interpolation) {
        for (Vector2 point : focalPoints) {
            if (target.dst(point) < threshold) {
                float newZoom = (target.dst(point) / threshold) + 0.2f;
                camera.zoom = camera.zoom + ((newZoom > 1 ? 1 : newZoom) - camera.zoom) * 0.1f;
                CameraStyles.lerpToTarget(camera, point, interpolation);
                return true;
            }
        }
        return false;
    }

    public static void shake(Camera camera, Vector2 displacement, float strength) {
        position.set(camera.position);
        position.x += displacement.x * strength;
        position.y += displacement.y * strength;
        camera.position.set(position);
        camera.update();
    }
}
