package com.mygdx.hadal.map;

import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.mygdx.hadal.actors.ModeSettingSelection;
import com.mygdx.hadal.actors.Text;
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.TiledObjectUtil;

/**
 * This mode setting is used for modes where the host can designate a number of lives.
 * @author Jergarita Jisrael
 */
public class SettingLives extends ModeSetting {

    public static final String[] livesChoices = {"UNLIMITED", "1 LIFE", "2 LIVES", "3 LIVES", "4 LIVES", "5 LIVES"};
    public static final String settingTag = "lives";
    public static final Integer defaultValue = 0;

    private SelectBox<String> livesOptions;

    @Override
    public void setSetting(PlayState state, GameMode mode, Table table) {
        Text lives = new Text("LIVES: ", 0, 0, false);
        lives.setScale(ModeSettingSelection.detailsScale);

        livesOptions = new SelectBox<>(GameStateManager.getSkin());
        livesOptions.setItems(livesChoices);
        livesOptions.setWidth(ModeSettingSelection.optionsWidth);
        livesOptions.setSelectedIndex(state.getGsm().getSetting().getModeSetting(mode, settingTag, defaultValue));

        table.add(lives);
        table.add(livesOptions).height(ModeSettingSelection.detailHeight).pad(ModeSettingSelection.detailPad).row();
    }

    @Override
    public void saveSetting(PlayState state, GameMode mode) {
        state.getGsm().getSetting().setModeSetting(mode, settingTag, livesOptions.getSelectedIndex());
    }

    @Override
    public String loadUIStart(PlayState state, GameMode mode) {
        int startLives = state.getGsm().getSetting().getModeSetting(mode, settingTag, defaultValue);
        if (startLives != 0) {
            return "LIVES";
        }
        return "";
    }

    @Override
    public String loadSettingStart(PlayState state, GameMode mode) {
        String gameLivesId = TiledObjectUtil.getPrefabTriggerId();

        int startLives = state.getGsm().getSetting().getModeSetting(mode, settingTag, defaultValue);

        if (startLives != 0) {
            RectangleMapObject game = new RectangleMapObject();
            game.setName("Game");
            game.getProperties().put("sync", "ALL");
            game.getProperties().put("changeTimer", false);
            game.getProperties().put("triggeredId", gameLivesId);

            game.getProperties().put("lives", startLives - 1);
            TiledObjectUtil.parseTiledEvent(state, game);

        } else {
            state.setUnlimitedLife(true);
        }
        return gameLivesId;
    }
}
