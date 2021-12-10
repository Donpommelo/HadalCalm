package com.mygdx.hadal.utils;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.hadal.managers.GameStateManager;

/**
 * These utils describe various tools for moving the camera.
 * @author Prelfmuffin Phokra
 */
public class CameraUtil {

    /**
     * This makes an input camera vector obey camera bounds.
     */
    public static void obeyCameraBounds(Vector2 tempCamera, OrthographicCamera camera, float[] cameraBounds) {

        float cameraWidth = camera.viewportWidth * camera.zoom / 2;
        float cameraHeight = camera.viewportHeight * camera.zoom / 2;

        if (cameraBounds[0] - 2 * cameraWidth < cameraBounds[1]) {
            tempCamera.x = (cameraBounds[0] + cameraBounds[1]) / 2;
        } else {
            if (tempCamera.x > cameraBounds[0] - cameraWidth) {
                tempCamera.x = cameraBounds[0] - cameraWidth;
            }
            if (tempCamera.x < cameraBounds[1] + cameraWidth) {
                tempCamera.x = cameraBounds[1] + cameraWidth;
            }
        }

        if (cameraBounds[2] - 2 * cameraHeight < cameraBounds[3]) {
            tempCamera.y = (cameraBounds[2] + cameraBounds[3]) / 2;
        } else {
            if (tempCamera.y > cameraBounds[2] - cameraHeight) {
                tempCamera.y = cameraBounds[2] - cameraHeight;
            }
            if (tempCamera.y < cameraBounds[3] + cameraHeight) {
                tempCamera.y = cameraBounds[3] + cameraHeight;
            }
        }
    }

    private final static Vector3 position = new Vector3();
    public static void lerpToTarget(Camera camera, Vector2 target, float interpolation) {
        // a + (b - a) * lerp factor
        position.set(camera.position);
        position.x = (int) (camera.position.x + (target.x - camera.position.x) * interpolation);
        position.y = (int) (camera.position.y + (target.y - camera.position.y) * interpolation);
        camera.position.set(position);
        camera.update();
    }

    private static final Vector2 maxOffset = new Vector2(140, 105);
    private static final float maxRotation = 2.5f;
    private static final float decay = 0.9f;
    private static float trauma, currentAngle;
    private static final float noiseSneed = MathUtils.random();
    private static float noise_y;
    public static void shake(OrthographicCamera camera, Vector2 tempCamera, float delta) {
        trauma = Math.max(0.0f, trauma - delta * decay);
        float amount = trauma * trauma;
        noise_y++;
        float rotation = maxRotation * amount * NoiseUtil.generateNoise(noiseSneed, noise_y) - currentAngle;
        tempCamera.x += maxOffset.x * amount * NoiseUtil.generateNoise(noiseSneed * 2, noise_y);
        tempCamera.y += maxOffset.y * amount * NoiseUtil.generateNoise(noiseSneed * 3, noise_y);

        currentAngle += rotation;
        camera.rotate(rotation);

        traumaCount = Math.max(0.0f, traumaCount - delta);
    }

    private static final float traumaMultiplier = 0.045f;
    private static final float traumaCooldown = 0.5f;
    private static final float minTrauma = 0.75f;
    private static float traumaCount;
    public static void inflictTrauma(GameStateManager gsm, float amount) {
        if (!gsm.getSetting().isScreenShake()) { return; }

        float adjustedAmount = amount * traumaMultiplier;
        if (adjustedAmount < minTrauma) {
            if (traumaCount <= 0.0f) {
                traumaCount = traumaCooldown;
                trauma = Math.min(1.0f, minTrauma);
            }
        } else {
            trauma = Math.min(1.0f, adjustedAmount + trauma);
        }
    }
}
