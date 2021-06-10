package com.mygdx.hadal.map;

import com.mygdx.hadal.states.PlayState;

/**
 * This mode setting simply designates the mode as one with pvp
 * @author Fulfram Frarbhead
 */
public class TogglePVP extends ModeSetting {

    @Override
    public void loadSettingMisc(PlayState state, GameMode mode) { state.setPvp(true); }
}
