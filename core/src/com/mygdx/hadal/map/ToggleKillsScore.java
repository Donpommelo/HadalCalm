package com.mygdx.hadal.map;

import com.mygdx.hadal.states.PlayState;

public class ToggleKillsScore extends ModeSetting {

    @Override
    public void loadSettingMisc(PlayState state) {
        state.setKillsScore(true);
    }
}
