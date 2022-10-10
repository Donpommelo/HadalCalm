package com.mygdx.hadal.map;

import com.mygdx.hadal.equip.Loadout;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.constants.Constants;

/**
 * This mode setting is used for modes where players do not inflict damage to one another.
 * @author Quizmeister Quifield
 */
public class ToggleNoDamage extends ModeSetting {

    @Override
    public void modifyNewPlayer(PlayState state, GameMode mode, Loadout newLoadout, Player p, short hitboxFilter) {
        p.setHitboxfilter(Constants.PLAYER_HITBOX);
    }
}
