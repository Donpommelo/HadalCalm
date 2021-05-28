package com.mygdx.hadal.map;

import com.mygdx.hadal.states.PlayState;

public class TogglePVP extends ModeSetting {

    @Override
    public void loadSettingMisc(PlayState state) { state.setPvp(true); }
}
