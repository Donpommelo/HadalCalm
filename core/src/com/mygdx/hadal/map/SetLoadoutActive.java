package com.mygdx.hadal.map;

import com.mygdx.hadal.save.UnlockActives;
import com.mygdx.hadal.states.PlayState;

/**
 * This mode setting is used for modes with a set active item
 * @author Nartabrooke Narjahead
 */
public class SetLoadoutActive extends ModeSetting {

    private final UnlockActives active;

    public SetLoadoutActive(UnlockActives active) {this.active = active; }

    public void loadSettingMisc(PlayState state, GameMode mode) {
        state.setMapActiveItem(active);
    }
}
