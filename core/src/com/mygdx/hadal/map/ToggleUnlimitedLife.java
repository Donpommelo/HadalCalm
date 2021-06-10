package com.mygdx.hadal.map;

import com.mygdx.hadal.states.PlayState;

/**
 * This mode setting simply designates the mode as one with no lives restrictions
 * @author Snotalini Swungo
 */
public class ToggleUnlimitedLife extends ModeSetting {

    @Override
    public void loadSettingMisc(PlayState state, GameMode mode) {
        state.setUnlimitedLife(true);
    }
}
