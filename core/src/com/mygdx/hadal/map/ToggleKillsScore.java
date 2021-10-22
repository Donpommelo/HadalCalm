package com.mygdx.hadal.map;

import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;

/**
 * This mode setting is used for modes where kills award score.
 * @author Fledorf Flidorf
 */
public class ToggleKillsScore extends ModeSetting {

    @Override
    public void processPlayerDeath(PlayState state, GameMode mode, Schmuck perp, Player vic, DamageTypes... tags) {
        if (perp instanceof Player) {
            mode.processPlayerScoreChange(state, (Player) perp, 1);
        }
    }
}
