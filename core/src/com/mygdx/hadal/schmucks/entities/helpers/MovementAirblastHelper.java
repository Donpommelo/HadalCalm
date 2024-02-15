package com.mygdx.hadal.schmucks.entities.helpers;

import com.mygdx.hadal.constants.Stats;
import com.mygdx.hadal.equip.misc.Airblaster;
import com.mygdx.hadal.schmucks.entities.Player;

/**
 * MovementAirblastHelper processes a player's airblast
 */
public class MovementAirblastHelper {

    private static final float AIRBLAST_CD = 0.25f;
    private static final float AIRBLAST_FUEL_REGEN_CD = 3.0f;
    private static final int AIRBLAST_COST = 25;

    private final Player player;

    //Equipment that the player has built into their toolset.
    private final Airblaster airblast;

    //keep track of cd before airblast can be used again (just to avoid accidental double airblast
    private float airblastCdCount;

    //If airblasting on cooldown, buffer an airblast to use once cooldown expires
    private boolean airblastBuffered;

    public MovementAirblastHelper(Player player) {
        this.player = player;

        this.airblast = new Airblaster(player);
    }

    public void controller(float delta) {
        airblastCdCount -= delta;

        //if an airblast is buffered, execute it once cooldown ends
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
            if (player.getPlayerData().getCurrentFuel() >= getAirblastCost()) {

                //airblasting sets fuel regen on cooldown
                if (player.getFuelHelper().getFuelRegenCdCount() < AIRBLAST_FUEL_REGEN_CD) {
                    player.getFuelHelper().setFuelRegenCdCount(AIRBLAST_FUEL_REGEN_CD);
                }

                player.getPlayerData().fuelSpend(getAirblastCost());
                airblastCdCount = AIRBLAST_CD;
                player.getShootHelper().shoot(0, airblast, false);
            }
        } else {
            airblastBuffered = true;
        }
    }

    public float getAirblastCost() { return AIRBLAST_COST * (1 + player.getPlayerData().getStat(Stats.BOOST_COST)); }
}
