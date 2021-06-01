package com.mygdx.hadal.map;

import com.mygdx.hadal.save.UnlockActives;
import com.mygdx.hadal.states.PlayState;

public class SetLoadoutActive extends ModeSetting {

    private final UnlockActives active;

    public SetLoadoutActive(UnlockActives active) {this.active = active; }

    public void loadSettingMisc(PlayState state) {
        state.setMapActiveItem(active);
    }
}
