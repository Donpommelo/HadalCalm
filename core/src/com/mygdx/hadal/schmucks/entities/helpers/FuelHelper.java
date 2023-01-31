package com.mygdx.hadal.schmucks.entities.helpers;

import com.mygdx.hadal.constants.Stats;
import com.mygdx.hadal.schmucks.entities.Player;

public class FuelHelper {

    private static final float FUEL_REGEN = 16.0f;
    private static final float GROUND_FUEL_CD_BOOST = 3.0f;
    private static final float GROUND_FUEL_REGEN_BOOST = 5.0f;

    private final Player player;

    private float fuelRegenCdCount;

    public FuelHelper(Player player) {
        this.player = player;
    }

    public void controller(float delta) {
        //process fuel regen. Base fuel regen is canceled upon using fuel.
        if (fuelRegenCdCount > 0.0f) {
            fuelRegenCdCount -= player.isGrounded() ? delta * GROUND_FUEL_CD_BOOST : delta;
        } else {
            player.getPlayerData().fuelGain(player.isGrounded() ? GROUND_FUEL_REGEN_BOOST * FUEL_REGEN * delta : FUEL_REGEN * delta);
        }
        player.getPlayerData().fuelGain(player.getPlayerData().getStat(Stats.FUEL_REGEN) * delta);
    }

    public float getFuelRegenCdCount() { return fuelRegenCdCount; }

    public void setFuelRegenCdCount(float fuelRegenCdCount) { this.fuelRegenCdCount = fuelRegenCdCount; }
}
