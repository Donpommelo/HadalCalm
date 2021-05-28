package com.mygdx.hadal.map;

import com.mygdx.hadal.states.PlayState;

public class ToggleNoDamage extends ModeSetting {

    @Override
    public void loadSettingMisc(PlayState state) {
        state.setNoDamage(true);
    }
}
