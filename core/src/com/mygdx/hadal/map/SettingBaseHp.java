package com.mygdx.hadal.map;

import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.mygdx.hadal.actors.ModeSettingSelection;
import com.mygdx.hadal.actors.Text;
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.states.PlayState;

/**
 * This mode setting is used for modes where the player can set their base Hp
 * @author Doltfield Desmith
 */
public class SettingBaseHp extends ModeSetting {

    private static final String[] hpChoices = {"100", "125", "150", "175", "200"};
    private static final String settingTag = "base_hp";
    private static final Integer defaultValue = 2;

    private SelectBox<String> hpOptions;

    @Override
    public void setSetting(PlayState state, GameMode mode, Table table) {
        Text hp = new Text("BASE HP: ", 0, 0, false);
        hp.setScale(ModeSettingSelection.detailsScale);

        hpOptions = new SelectBox<>(GameStateManager.getSkin());
        hpOptions.setItems(hpChoices);
        hpOptions.setWidth(ModeSettingSelection.optionsWidth);
        hpOptions.setSelectedIndex(state.getGsm().getSetting().getModeSetting(mode, settingTag, defaultValue));

        table.add(hp);
        table.add(hpOptions).height(ModeSettingSelection.detailHeight).pad(ModeSettingSelection.detailPad).row();
    }

    @Override
    public void saveSetting(PlayState state, GameMode mode) {
        state.getGsm().getSetting().setModeSetting(mode, settingTag, hpOptions.getSelectedIndex());
    }

    @Override
    public void loadSettingMisc(PlayState state, GameMode mode) {
        state.setBaseHp(state.getGsm().getSetting().getModeSetting(mode, settingTag, defaultValue));
    }

    public static int indexToHp(int index) {
        return switch (index) {
            case 1 -> 125;
            case 2 -> 150;
            case 3 -> 175;
            case 4 -> 200;
            default -> 100;
        };
    }
}
