package com.mygdx.hadal.schmucks.entities.helpers;

import com.mygdx.hadal.schmucks.entities.Player;

public class MovementFastfallHelper {

    private static final float FAST_FALL_CD = 0.05f;

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
            if (player.getPlayerData().getFastFallPower() > 0) {
                player.push(0, -1, player.getPlayerData().getFastFallPower());
            }
        }
        if (!player.getFeetData().getTerrain().isEmpty()) {
            player.getFeetData().getTerrain().get(0).getEventData().onInteract(player);
        }
    }

    public boolean isFastFalling() { return fastFalling; }

    public void setFastFalling(boolean fastFalling) { this.fastFalling = fastFalling; }
}
