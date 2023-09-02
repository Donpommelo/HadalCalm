package com.mygdx.hadal.schmucks.entities.helpers;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.constants.Stats;
import com.mygdx.hadal.schmucks.entities.Player;

public class MovementFastfallHelper {

    private static final float FAST_FALL_CD = 0.05f;
    private static final float FAST_FALL_POW = 42.0f;

    private static final float FAST_FALL_MAX_THRESHOLD = -30.0f;

    private final Player player;

    private float fastFallCdCount;
    private boolean fastFalling;

    public MovementFastfallHelper(Player player) {
        this.player = player;
    }

    public void controllerInterval(Vector2 playerVelocity) {
        if (fastFalling) {
            fastFall(playerVelocity);
        }
    }

    public void controller(float delta) {
        fastFallCdCount -= delta;
    }

    /**
     * Player falls rapidly if in the air. If grounded, this also interacts with terrain events.
     */
    public void fastFall(Vector2 playerVelocity) {
        if (fastFallCdCount < 0) {
            fastFallCdCount = FAST_FALL_CD;
            if (getFastFallPower(playerVelocity) > 0) {
                player.push(0, -1, getFastFallPower(playerVelocity));
            }
        }
        if (!player.getFeetData().getTerrain().isEmpty()) {
            player.getFeetData().getTerrain().get(0).getEventData().onInteract(player);
        }
    }

    public float getFastFallPower(Vector2 playerVelocity) {

        float modifiedThreshold = FAST_FALL_MAX_THRESHOLD * (1 + player.getPlayerData().getStat(Stats.FASTFALL_POW));
        float modifiedVelocity = MathUtils.clamp(playerVelocity.y, modifiedThreshold, 0);

        float fastFall = FAST_FALL_POW * (1 - modifiedVelocity / FAST_FALL_MAX_THRESHOLD);

        return fastFall * (1 + player.getPlayerData().getStat(Stats.FASTFALL_POW));
    }

    public boolean isFastFalling() { return fastFalling; }

    public void setFastFalling(boolean fastFalling) { this.fastFalling = fastFalling; }
}
