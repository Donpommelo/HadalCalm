package com.mygdx.hadal.map;

import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.mygdx.hadal.actors.ModeSettingSelection;
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.states.PlayState;

/**
 * This mode setting is used for pvp modes where players can see each other's Hp
 */
public class SettingVisibleHp extends ModeSetting {

    private static final String settingTag = "visible_hp";
    private static final Integer defaultValue = 0;

    private CheckBox dropsOptions;

    @Override
    public void setSetting(PlayState state, GameMode mode, Table table) {
        dropsOptions = new CheckBox("VISIBLE ENEMY HP?",GameStateManager.getSkin());
        dropsOptions.setChecked(state.getGsm().getSetting().getModeSetting(mode, settingTag, defaultValue) == 1);
        table.add(dropsOptions).height(ModeSettingSelection.detailHeight).pad(ModeSettingSelection.detailPad).top().row();
    }

    @Override
    public void saveSetting(PlayState state, GameMode mode) {
        state.getGsm().getSetting().setModeSetting(mode, settingTag, dropsOptions.isChecked() ? 1 : 0);
    }

    @Override
    public void loadSettingMisc(PlayState state, GameMode mode) {
        if (state.getGsm().getSetting().getModeSetting(mode, settingTag, defaultValue) == 1) {
            state.setVisibleHp(true);
        }
    }
}
