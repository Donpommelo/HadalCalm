package com.mygdx.hadal.bots;

import com.badlogic.gdx.math.MathUtils;

public class BotPersonality {

    private static final float mouseAimSpeedDefault = 0.075f;
    private static final float visionXDefault = 30.0f;
    private static final float visionYDefault = 16.0f;
    private static final float boostThreshold = 250.0f;
    private static final float boostDesireRange = 0.2f;
    private static final float weaponDesireRange = 0.4f;
    private static final float healthDesireRange = 0.5f;
    private static final float violenceDesireRange = 0.25f;
    private static final float chatWheelDesireMin = -0.7f;
    private static final float chatWheelDesireMax = 0.3f;
    private final float mouseAimSpeed, visionX, visionY, boostDesireMultiplier, weaponDesireMultiplier,
            healthDesireMultiplier, violenceDesireMultiplier, chatWheelDesire, wobbleMin, wobbleMax, wobbleDecrement,
    wobbleIncrement;

    public BotPersonality(BotDifficulty difficulty) {
        mouseAimSpeed = mouseAimSpeedDefault * (1.0f + difficulty.mouseAimSpeed);
        visionX = visionXDefault * (1.0f + difficulty.visionRange);
        visionY = visionYDefault * (1.0f + difficulty.visionRange);
        wobbleMin = difficulty.wobbleMin;
        wobbleMax = difficulty.wobbleMax;
        wobbleDecrement = difficulty.wobbleDecrement;
        wobbleIncrement = difficulty.wobbleIncrement;

        boostDesireMultiplier = boostThreshold * (1.0f + MathUtils.random(-boostDesireRange, boostDesireRange));
        weaponDesireMultiplier = MathUtils.random(-weaponDesireRange, weaponDesireRange);
        healthDesireMultiplier = MathUtils.random(-healthDesireRange, healthDesireRange);
        violenceDesireMultiplier = MathUtils.random(-violenceDesireRange, violenceDesireRange);
        chatWheelDesire = MathUtils.random(chatWheelDesireMin, chatWheelDesireMax);
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
