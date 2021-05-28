package com.mygdx.hadal.map;

import com.mygdx.hadal.states.PlayState;

public class ModeSetting {

    private GameMode mode;

    public void setSetting(GameMode mode) {
        this.mode = mode;
    }

    public String loadSettingStart(PlayState state) { return ""; }

    public String loadSettingSpawn(PlayState state) { return ""; }

    public String loadSettingMisc(PlayState state) { return ""; }

    public GameMode getMode() { return mode; }
}
