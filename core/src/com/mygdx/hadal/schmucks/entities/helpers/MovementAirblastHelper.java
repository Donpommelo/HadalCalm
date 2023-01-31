package com.mygdx.hadal.schmucks.entities.helpers;

import com.mygdx.hadal.equip.misc.Airblaster;
import com.mygdx.hadal.schmucks.entities.Player;

public class MovementAirblastHelper {

    private static final float AIRBLAST_CD = 0.25f;
    private static final float AIRBLAST_FUEL_REGEN_CD = 3.0f;

    private final Player player;

    //Equipment that the player has built into their toolset.
    private final Airblaster airblast;

    private float airblastCdCount;
    private boolean airblastBuffered;

    public MovementAirblastHelper(Player player) {
        this.player = player;

        this.airblast = new Airblaster(player);
    }

    public void controller(float delta) {
        airblastCdCount -= delta;

        if (airblastBuffered && airblastCdCount < 0) {
            airblastBuffered = false;
            airblast();
        }
    }

    /**
     * Player's airblast power. Boosts player, knocks enemies/hitboxes.
     */
    public void airblast() {
        if (airblastCdCount < 0) {
            if (player.getPlayerData().getCurrentFuel() >= player.getPlayerData().getAirblastCost()) {

                //airblasting sets fuel regen on cooldown
                if (player.getFuelHelper().getFuelRegenCdCount() < AIRBLAST_FUEL_REGEN_CD) {
                    player.getFuelHelper().setFuelRegenCdCount(AIRBLAST_FUEL_REGEN_CD);
                }

                player.getPlayerData().fuelSpend(player.getPlayerData().getAirblastCost());
                airblastCdCount = AIRBLAST_CD;
                player.useToolStart(0, airblast, player.getHitboxFilter(), player.getMouseHelper().getPixelPosition(), false);
            }
        } else {
            airblastBuffered = true;
        }
    }
}
