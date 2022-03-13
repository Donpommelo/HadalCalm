package com.mygdx.hadal.bots;

public class BotPersonality {

    private static final float mouseAimSpeedDefault = 0.075f;
    private static final float visionXDefault = 30.0f;
    private static final float visionYDefault = 16.0f;
    private final float mouseAimSpeed, visionX, visionY;

    public BotPersonality(BotDifficulty difficulty) {
        mouseAimSpeed = mouseAimSpeedDefault * (1.0f + difficulty.mouseAimSpeed);
        visionX = visionXDefault * (1.0f + difficulty.visionRange);
        visionY = visionYDefault * (1.0f + difficulty.visionRange);
    }

    public float getMouseAimSpeed() { return mouseAimSpeed; }

    public float getVisionX() { return visionX; }

    public float getVisionY() { return visionY; }

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
