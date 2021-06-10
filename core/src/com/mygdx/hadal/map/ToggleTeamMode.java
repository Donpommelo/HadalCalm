package com.mygdx.hadal.map;

import com.mygdx.hadal.states.PlayState;

/**
 * This mode setting designates the default team mode of the mode
 * @author Yenerd Yecaster
 */
public class ToggleTeamMode extends ModeSetting {

    private final int teamMode;

    public ToggleTeamMode(int teamMode) {
        this.teamMode = teamMode;
    }

    @Override
    public void loadSettingMisc(PlayState state, GameMode mode) {
        state.setTeamMode(teamMode);
    }
}
