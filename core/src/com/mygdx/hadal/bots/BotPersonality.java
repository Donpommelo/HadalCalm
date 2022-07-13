package com.mygdx.hadal.bots;

import com.badlogic.gdx.math.MathUtils;

/**
 * A BotPersonality controls the variance in a BotPlayer's behavior, as well as their difficulty setting
 * @author Flatthoff Furgeon
 */
public class BotPersonality {

    private static final float MOUSE_AIM_SPEED_DEFAULT = 0.075f;
    private static final float VISION_X_DEFAULT = 30.0f;
    private static final float VISION_Y_DEFAULT = 16.0f;
    private static final float BOOST_THRESHOLD = 250.0f;
    private static final float BOOST_DESIRE_RANGE = 0.2f;
    private static final float WEAPON_DESIRE_RANGE = 0.4f;
    private static final float HEALTH_DESIRE_RANGE = 0.5f;
    private static final float VIOLENCE_DESIRE_RANGE = 0.25f;
    private static final float CHAT_WHEEL_DESIRE_MIN = -0.7f;
    private static final float CHAT_WHEEL_DESIRE_MAX = 0.3f;
    private final float mouseAimSpeed, visionX, visionY, boostDesireMultiplier, weaponDesireMultiplier,
            healthDesireMultiplier, violenceDesireMultiplier, chatWheelDesire, wobbleMin, wobbleMax, wobbleDecrement,
    wobbleIncrement;

    public BotPersonality(BotDifficulty difficulty) {
        mouseAimSpeed = MOUSE_AIM_SPEED_DEFAULT * (1.0f + difficulty.mouseAimSpeed);
        visionX = VISION_X_DEFAULT * (1.0f + difficulty.visionRange);
        visionY = VISION_Y_DEFAULT * (1.0f + difficulty.visionRange);
        wobbleMin = difficulty.wobbleMin;
        wobbleMax = difficulty.wobbleMax;
        wobbleDecrement = difficulty.wobbleDecrement;
        wobbleIncrement = difficulty.wobbleIncrement;

        boostDesireMultiplier = BOOST_THRESHOLD * (1.0f + MathUtils.random(-BOOST_DESIRE_RANGE, BOOST_DESIRE_RANGE));
        weaponDesireMultiplier = MathUtils.random(-WEAPON_DESIRE_RANGE, WEAPON_DESIRE_RANGE);
        healthDesireMultiplier = MathUtils.random(-HEALTH_DESIRE_RANGE, HEALTH_DESIRE_RANGE);
        violenceDesireMultiplier = MathUtils.random(-VIOLENCE_DESIRE_RANGE, VIOLENCE_DESIRE_RANGE);
        chatWheelDesire = MathUtils.random(CHAT_WHEEL_DESIRE_MIN, CHAT_WHEEL_DESIRE_MAX);
    }

    public float getMouseAimSpeed() { return mouseAimSpeed; }

    public float getVisionX() { return visionX; }

    public float getVisionY() { return visionY; }

    public float getWobbleMax() { return wobbleMax; }

    public float getWobbleMin() { return wobbleMin; }

    public float getWobbleDecrement() { return wobbleDecrement; }

    public float getWobbleIncrement() { return wobbleIncrement; }

    public float getBoostDesireMultiplier() { return boostDesireMultiplier; }

    public float getWeaponDesireMultiplier() { return weaponDesireMultiplier; }

    public float getHealthDesireMultiplier() { return healthDesireMultiplier; }

    public float getViolenceDesireMultiplier() { return violenceDesireMultiplier; }

    public float getChatWheelDesire() { return chatWheelDesire; }

    /**
     * BotDifficulty controls things like the bot's aim and vision
     */
    public enum BotDifficulty {
        EASY(0.0f, 0.0f, 10.0f, 50.0f, 1.5f, 40.0f),
        MEDIUM(2.5f, 0.1f, 5.0f, 25.0f, 2.5f, 5.0f),
        HARD(5.75f, 0.2f, 1.0f, 10.0f, 4.0f, 1.0f);

        private final float mouseAimSpeed, visionRange;
        private final float wobbleMin, wobbleMax, wobbleDecrement, wobbleIncrement;

        BotDifficulty(float mouseAimSpeed, float visionRange, float wobbleMin, float wobbleMax,
                      float wobbleDecrement, float wobbleIncrement) {
            this.mouseAimSpeed = mouseAimSpeed;
            this.visionRange = visionRange;
            this.wobbleMax = wobbleMax;
            this.wobbleMin = wobbleMin;
            this.wobbleDecrement = wobbleDecrement;
            this.wobbleIncrement = wobbleIncrement;
        }
    }
}
