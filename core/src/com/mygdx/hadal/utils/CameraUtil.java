package com.mygdx.hadal.utils;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.hadal.constants.Stats;
import com.mygdx.hadal.managers.JSONManager;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;

/**
 * These utils describe various tools for moving the camera.
 * @author Prelfmuffin Phokra
 */
public class CameraUtil {

    /**
     * This makes an input camera vector obey camera bounds.
     * We want to check the edges of camera vision, taking into account current zoom amount.
     * cameraBounds contains 4 values; right/left/up/down bounds respectively
     */
    public static void obeyCameraBounds(Vector2 tempCamera, OrthographicCamera camera, float[] cameraBounds) {
        float cameraWidth = camera.viewportWidth * camera.zoom;
        float cameraHeight = camera.viewportHeight * camera.zoom;

        if (cameraBounds[0] - cameraWidth < cameraBounds[1]) {
            tempCamera.x = (cameraBounds[0] + cameraBounds[1]) / 2;
        } else {
            if (tempCamera.x > cameraBounds[0] - cameraWidth / 2) {
                tempCamera.x = cameraBounds[0] - cameraWidth / 2;
            }
            if (tempCamera.x < cameraBounds[1] + cameraWidth / 2) {
                tempCamera.x = cameraBounds[1] + cameraWidth / 2;
            }
        }

        if (cameraBounds[2] - cameraHeight < cameraBounds[3]) {
            tempCamera.y = (cameraBounds[2] + cameraBounds[3]) / 2;
        } else {
            if (tempCamera.y > cameraBounds[2] - cameraHeight / 2) {
                tempCamera.y = cameraBounds[2] - cameraHeight / 2;
            }
            if (tempCamera.y < cameraBounds[3] + cameraHeight / 2) {
                tempCamera.y = cameraBounds[3] + cameraHeight / 2;
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

    //The max amount of screenshake displacement and rotation (in radians)
    private static final Vector2 MAX_OFFSET = new Vector2(140, 105);
    private static final float MAX_ROTATION = 2.5f;

    //This represents the rate that the screenshake decreases
    private static final float DECAY = 0.9f;

    //"trauma" is the current shake amount. It is increased by things like receiving damage and decreases over time
    private static float trauma, currentAngle;

    //This is a random seed used to generate noise
    private static final float NOISE_SNEED = MathUtils.random();
    private static float noise_y;
    /**
     * This processes screen shake and is run when we update the game camera
     * @param camera: the camera that is shaking
     * @param tempCamera: This vector holds the camera's tentative target position
     * @param delta: This si the amount of game time that has passed
     */
    public static void shake(OrthographicCamera camera, Vector2 tempCamera, float delta) {

        //decrement trauma according to time and use it to calculate the amount of shaking
        trauma = Math.max(0.0f, trauma - delta * DECAY);
        float amount = trauma * trauma;

        //increment noise and use it to find the camera's random displacement and rotation
        noise_y++;
        float rotation = MAX_ROTATION * amount * NoiseUtil.generateNoise(NOISE_SNEED, noise_y) - currentAngle;
        tempCamera.x += MAX_OFFSET.x * amount * NoiseUtil.generateNoise(NOISE_SNEED * 2, noise_y);
        tempCamera.y += MAX_OFFSET.y * amount * NoiseUtil.generateNoise(NOISE_SNEED * 3, noise_y);

        //currentAngle is used to keep track of the camera's angle, since rotate() rotates it a set amount
        //we subtract currentAngle when calculating rotation to rotate to an angle, not by an angle
        currentAngle += rotation;
        camera.rotate(rotation);

        //decrement trauma cooldown for small instances of damage so continuous damage doesn't cause excessive shaking
        traumaCount = Math.max(0.0f, traumaCount - delta);
    }

    private static final float TRAUMA_MULTIPLIER = 0.045f;
    private static final float TRAUMA_COOLDOWN = 0.5f;
    private static final float MIN_TRAUMA = 0.75f;
    private static float traumaCount;
    /**
     * This adds some additive screen-shake
     * @param amount: amount of damage (or equivalent metric) of screen-shake to add
     */
    public static void inflictTrauma(float amount, PlayerBodyData player) {
        if (!JSONManager.setting.isScreenShake() &&
                (null == player || 0 == player.getStat(Stats.CAMERA_SHAKE))) { return; }

        float shakeBonus = 1.0f;
        if (null != player) {
            shakeBonus = 1.0f + player.getStat(Stats.CAMERA_SHAKE);
        }

        float adjustedAmount = amount * TRAUMA_MULTIPLIER * shakeBonus;

        //small instances of trauma are set to a constant value but have a cooldown
        if (MIN_TRAUMA > adjustedAmount) {
            if (0.0f <= traumaCount) {
                traumaCount = TRAUMA_COOLDOWN;
                trauma = Math.min(shakeBonus, MIN_TRAUMA * shakeBonus);
            }
        } else {
            trauma = Math.min(shakeBonus, adjustedAmount + trauma);
        }
    }

    /**
     * This resets camera shake when a playstate is disposed
     * This prevents leftover screenshake from persisting in the next playstate
     */
    public static void resetCameraRotation(OrthographicCamera camera) {
        camera.rotate(-currentAngle);
        camera.direction.set(0, 0, -1);
        camera.up.set(0, 1, 0);
        currentAngle = 0;
        trauma = 0;
        traumaCount = 0;
    }
}
