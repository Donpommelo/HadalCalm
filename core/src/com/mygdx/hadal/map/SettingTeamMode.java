package com.mygdx.hadal.map;

import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.mygdx.hadal.actors.ModeSelection;
import com.mygdx.hadal.actors.Text;
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.states.PlayState;

public class SettingTeamMode extends ModeSetting {

    public static final String[] teamChoices = {"FREE_FOR_ALL", "AUTO_ASSIGN", "MANUAL ASSIGN"};
    private static final String settingTag = "team_mode";
    private static final Integer defaultValue = 0;

    private SelectBox<String> teamsOptions;

    @Override
    public void setSetting(PlayState state, GameMode mode, Table table) {
        Text team = new Text("TEAM MODE: ", 0, 0, false);
        team.setScale(ModeSelection.detailsScale);

        teamsOptions = new SelectBox<>(GameStateManager.getSkin());
        teamsOptions.setItems(teamChoices);
        teamsOptions.setWidth(ModeSelection.optionsWidth);
        teamsOptions.setSelectedIndex(state.getGsm().getSetting().getModeSetting(mode, settingTag, defaultValue));

        table.add(team);
        table.add(teamsOptions).height(ModeSelection.detailHeight).pad(ModeSelection.detailPad).row();
    }

    @Override
    public void saveSetting(PlayState state, GameMode mode) {
        state.getGsm().getSetting().setModeSetting(mode, settingTag, teamsOptions.getSelectedIndex());
    }

    @Override
    public void loadSettingMisc(PlayState state, GameMode mode) {
        state.setTeamMode(state.getGsm().getSetting().getModeSetting(mode, settingTag, defaultValue));
    }
}
