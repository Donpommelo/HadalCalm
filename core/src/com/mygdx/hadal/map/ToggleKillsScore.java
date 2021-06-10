package com.mygdx.hadal.map;

import com.mygdx.hadal.states.PlayState;

/**
 * This mode setting is used for modes where kills award score.
 * @author Fledorf Flidorf
 */
public class ToggleKillsScore extends ModeSetting {

    @Override
    public void loadSettingMisc(PlayState state, GameMode mode) {
        state.setKillsScore(true);
    }
}
