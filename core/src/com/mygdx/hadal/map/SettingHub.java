package com.mygdx.hadal.map;

import com.mygdx.hadal.states.PlayState;

/**
 * This mode setting is used for the hub so that ending arcade mode and returning to the hub lets the game know we are
 * not in arcade mode anymore
 */
public class SettingHub extends ModeSetting {

    @Override
    public void loadSettingMisc(PlayState state, GameMode mode) {
        SettingArcade.arcade = false;
    }
}
