package com.mygdx.hadal.schmucks.entities.helpers;

import com.badlogic.gdx.math.MathUtils;
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

    public void controllerInterval() {
        if (fastFalling) {
            fastFall();
        }
    }

    public void controller(float delta) {
        fastFallCdCount -= delta;
    }

    /**
     * Player falls rapidly if in the air. If grounded, this also interacts with terrain events.
     */
    public void fastFall() {
        if (fastFallCdCount < 0) {
            fastFallCdCount = FAST_FALL_CD;
            if (getFastFallPower() > 0) {
                player.push(0, -1, getFastFallPower());
            }
        }
        if (!player.getFeetData().getTerrain().isEmpty()) {
            player.getFeetData().getTerrain().get(0).getEventData().onInteract(player);
        }
    }

    public float getFastFallPower() {

        float modifiedThreshold = FAST_FALL_MAX_THRESHOLD * (1 + player.getPlayerData().getStat(Stats.FASTFALL_POW));
        float modifiedVelocity = MathUtils.clamp(player.getLinearVelocity().y, modifiedThreshold, 0);

        float fastFall = FAST_FALL_POW * (1 - modifiedVelocity / FAST_FALL_MAX_THRESHOLD);

        return fastFall * (1 + player.getPlayerData().getStat(Stats.FASTFALL_POW));
    }

    public boolean isFastFalling() { return fastFalling; }

    public void setFastFalling(boolean fastFalling) { this.fastFalling = fastFalling; }
}
