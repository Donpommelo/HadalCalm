package com.mygdx.hadal.map;

import com.mygdx.hadal.states.PlayState;

/**
 * This mode setting is used for modes where eggplants are spawned.
 * @author Twonkeldebeast Twidah
 */
public class ToggleEggplantDrops extends ModeSetting {

    @Override
    public void loadSettingMisc(PlayState state, GameMode mode) {
        state.setEggplantDrops(true);
    }
}
