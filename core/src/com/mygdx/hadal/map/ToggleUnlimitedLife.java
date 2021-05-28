package com.mygdx.hadal.map;

import com.mygdx.hadal.states.PlayState;

public class ToggleUnlimitedLife extends ModeSetting {

    @Override
    public void loadSettingMisc(PlayState state) {
        state.setUnlimitedLife(true);
    }
}
