package com.mygdx.hadal.map;

import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.mygdx.hadal.actors.ModeSettingSelection;
import com.mygdx.hadal.actors.Text;
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.states.PlayState;

/**
 * This mode setting is used for modes where the player can set their respawn time
 */
public class SettingRespawnTime extends ModeSetting {

    private static final String[] respawnChoices = {"1s", "2s", "3s", "4s", "5s", "6s", "7s", "8s"};
    private static final String settingTag = "respawn_time";
    private static final Integer defaultValue = 1;

    private SelectBox<String> respawnOptions;

    @Override
    public void setSetting(PlayState state, GameMode mode, Table table) {
        Text respawn = new Text("Respawn Time: ", 0, 0, false);
        respawn.setScale(ModeSettingSelection.detailsScale);

        respawnOptions = new SelectBox<>(GameStateManager.getSkin());
        respawnOptions.setItems(respawnChoices);
        respawnOptions.setWidth(ModeSettingSelection.optionsWidth);
        respawnOptions.setSelectedIndex(state.getGsm().getSetting().getModeSetting(mode, settingTag, defaultValue));

        table.add(respawn);
        table.add(respawnOptions).height(ModeSettingSelection.detailHeight).pad(ModeSettingSelection.detailPad).row();
    }

    @Override
    public void saveSetting(PlayState state, GameMode mode) {
        state.getGsm().getSetting().setModeSetting(mode, settingTag, respawnOptions.getSelectedIndex());
    }

    @Override
    public void loadSettingMisc(PlayState state, GameMode mode) {
        state.setRespawnTime(indexToRespawnTime(state.getGsm().getSetting().getModeSetting(mode, settingTag, defaultValue)));
    }

    private float indexToRespawnTime(int index) {
        return switch (index) {
            case 1 -> 1.5f;
            case 2 -> 2.5f;
            case 3 -> 3.5f;
            case 4 -> 4.5f;
            case 5 -> 5.5f;
            case 6 -> 6.5f;
            case 7 -> 7.5f;
            default -> 0.5f;
        };
    }
}
