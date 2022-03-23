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
            healthDesireMultiplier, violenceDesireMultiplier, chatWheelDesire;

    public BotPersonality(BotDifficulty difficulty) {
        mouseAimSpeed = mouseAimSpeedDefault * (1.0f + difficulty.mouseAimSpeed);
        visionX = visionXDefault * (1.0f + difficulty.visionRange);
        visionY = visionYDefault * (1.0f + difficulty.visionRange);

        boostDesireMultiplier = boostThreshold * (1.0f + MathUtils.random(-boostDesireRange, boostDesireRange));
        weaponDesireMultiplier = MathUtils.random(-weaponDesireRange, weaponDesireRange);
        healthDesireMultiplier = MathUtils.random(-healthDesireRange, healthDesireRange);
        violenceDesireMultiplier = MathUtils.random(-violenceDesireRange, violenceDesireRange);
        chatWheelDesire = MathUtils.random(chatWheelDesireMin, chatWheelDesireMax);
    }

    public float getMouseAimSpeed() { return mouseAimSpeed; }

    public float getVisionX() { return visionX; }

    public float getVisionY() { return visionY; }

    public float getBoostDesireMultiplier() { return boostDesireMultiplier; }

    public float getWeaponDesireMultiplier() { return weaponDesireMultiplier; }

    public float getHealthDesireMultiplier() { return healthDesireMultiplier; }

    public float getViolenceDesireMultiplier() { return violenceDesireMultiplier; }

    public float getChatWheelDesire() { return chatWheelDesire; }

    public enum BotDifficulty {
        EASY(0.0f, 0.0f),
        MEDIUM(2.5f, 0.1f),
        HARD(5.75f, 0.2f);

        private final float mouseAimSpeed, visionRange;

        BotDifficulty(float mouseAimSpeed, float visionRange) {
            this.mouseAimSpeed = mouseAimSpeed;
            this.visionRange = visionRange;
        }
    }
}
