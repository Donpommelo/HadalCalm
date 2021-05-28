package com.mygdx.hadal.map;

import com.mygdx.hadal.states.PlayState;

public class ToggleTeamMode extends ModeSetting {

    private int teamMode;

    public ToggleTeamMode(int teamMode) {
        this.teamMode = teamMode;
    }

    @Override
    public void loadSettingMisc(PlayState state) {
        state.setTeamMode(teamMode);
    }
}
