package com.mygdx.hadal.map;

import com.mygdx.hadal.states.PlayState;

/**
 * This mode setting is used for modes where the player can set their base Hp
 * @author Doltfield Desmith
 */
public class SettingHub extends ModeSetting {

    @Override
    public void loadSettingMisc(PlayState state, GameMode mode) {
        SettingArcade.arcade = false;
    }
}
