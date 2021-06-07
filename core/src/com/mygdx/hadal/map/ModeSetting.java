package com.mygdx.hadal.map;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.mygdx.hadal.states.PlayState;

public class ModeSetting {

    public void setSetting(PlayState state, GameMode mode, Table table) {}

    public void saveSetting(PlayState state, GameMode mode) {}

    public String loadSettingStart(PlayState state, GameMode mode) { return ""; }

    public String loadUIStart(PlayState state, GameMode mode) { return ""; }

    public String loadSettingSpawn(PlayState state, GameMode mode) { return ""; }

    public void loadSettingMisc(PlayState state, GameMode mode) {}
}
