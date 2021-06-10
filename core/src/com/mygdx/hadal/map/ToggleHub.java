package com.mygdx.hadal.map;

import com.mygdx.hadal.states.PlayState;

/**
 * This mode setting indicates that this mode is a "hub" mode
 * @author Gicciatello Grumpernickel
 */
public class ToggleHub extends ModeSetting {

    @Override
    public void loadSettingMisc(PlayState state, GameMode mode) {
        state.setHub(true);
    }
}
