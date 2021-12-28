package com.mygdx.hadal.map;

import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.mygdx.hadal.actors.ModeSettingSelection;
import com.mygdx.hadal.actors.Text;
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.text.HText;
import com.mygdx.hadal.utils.TiledObjectUtil;

/**
 * This mode setting is used for modes where the host can designate a time limit.
 * When the time expires, the player or team with the most points wins
 * @author Blashutanga Bluryl
 */
public class SettingTimer extends ModeSetting {

    private final String endText;
    public static final String settingTag = "timer";
    public static final Integer defaultValue = 5;

    private SelectBox<String> timerOptions;

    public SettingTimer(String endText) {
        this.endText = endText;
    }

    @Override
    public void setSetting(PlayState state, GameMode mode, Table table) {
        String[] timerChoices = HText.SETTING_TIMER_OPTIONS.text().split(",");
        Text timer = new Text(HText.SETTING_TIMER.text());
        timer.setScale(ModeSettingSelection.detailsScale);

        timerOptions = new SelectBox<>(GameStateManager.getSkin());
        timerOptions.setItems(timerChoices);
        timerOptions.setWidth(ModeSettingSelection.optionsWidth);
        timerOptions.setSelectedIndex(state.getGsm().getSetting().getModeSetting(mode, settingTag, defaultValue));

        table.add(timer);
        table.add(timerOptions).height(ModeSettingSelection.detailHeight).pad(ModeSettingSelection.detailPad).row();
    }

    @Override
    public void saveSetting(PlayState state, GameMode mode) {
        state.getGsm().getSetting().setModeSetting(mode, settingTag, timerOptions.getSelectedIndex());
    }

    @Override
    public String loadUIStart(PlayState state, GameMode mode) {
        int startTimer = state.getGsm().getSetting().getModeSetting(mode, settingTag, defaultValue);
        if (startTimer != 0) {
            return "TIMER";
        }
        return "";
    }

    @Override
    public String loadSettingStart(PlayState state, GameMode mode) {

        int startTimer = state.getGsm().getSetting().getModeSetting(mode, settingTag, defaultValue);

        if (startTimer != 0) {
            state.getUiExtra().changeTimer(indexToTimer(startTimer), -1.0f);
        }

        RectangleMapObject end = new RectangleMapObject();
        end.setName("End");
        end.getProperties().put("text", endText);
        end.getProperties().put("triggeredId", "runOnGlobalTimerConclude");

        TiledObjectUtil.parseTiledEvent(state, end);

        return "";
    }

    /**
     * Convert timer from index in list to actual time amount
     */
    private float indexToTimer(int index) {
        return index * 60.f;
    }
}
