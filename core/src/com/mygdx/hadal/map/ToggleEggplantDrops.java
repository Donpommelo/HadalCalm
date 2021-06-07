package com.mygdx.hadal.map;

import com.mygdx.hadal.states.PlayState;

public class ToggleEggplantDrops extends ModeSetting {

    @Override
    public void loadSettingMisc(PlayState state, GameMode mode) {
        state.setEggplantDrops(true);
    }
}
