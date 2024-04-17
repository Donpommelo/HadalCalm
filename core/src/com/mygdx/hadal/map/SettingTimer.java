package com.mygdx.hadal.map;

import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.mygdx.hadal.actors.Text;
import com.mygdx.hadal.actors.UIHub;
import com.mygdx.hadal.managers.JSONManager;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.text.UIText;
import com.mygdx.hadal.utils.TiledObjectUtil;

import static com.mygdx.hadal.managers.SkinManager.SKIN;

/**
 * This mode setting is used for modes where the host can designate a time limit.
 * When the time expires, the player or team with the most points wins
 * @author Blashutanga Bluryl
 */
public class SettingTimer extends ModeSetting {

    private final String endText;
    private SelectBox<String> timerOptions;

    private final Integer defaultValue;

    public SettingTimer(String endText, int defaultValue) {
        this.defaultValue = defaultValue;
        this.endText = endText;
    }

    public SettingTimer(String endText) {
        this(endText, SettingSave.TIMER.getStartingValue());
    }

    @Override
    public void setSetting(PlayState state, GameMode mode, Table table) {
        String[] timerChoices = UIText.SETTING_TIMER_OPTIONS.text().split(",");
        Text timer = new Text(UIText.SETTING_TIMER.text());
        timer.setScale(UIHub.DETAILS_SCALE);

        timerOptions = new SelectBox<>(SKIN);
        timerOptions.setItems(timerChoices);
        timerOptions.setWidth(UIHub.OPTIONS_WIDTH);
        timerOptions.setSelectedIndex(JSONManager.setting.getModeSetting(mode, SettingSave.TIMER, defaultValue));

        table.add(timer);
        table.add(timerOptions).height(UIHub.DETAIL_HEIGHT).pad(UIHub.DETAIL_PAD).row();
    }

    @Override
    public void saveSetting(PlayState state, GameMode mode) {
        JSONManager.setting.setModeSetting(mode, SettingSave.TIMER, timerOptions.getSelectedIndex());
    }

    @Override
    public String loadUIStart(PlayState state, GameMode mode) {
        int startTimer = JSONManager.setting.getModeSetting(mode, SettingSave.TIMER, defaultValue);
        if (startTimer != 0) {
            return "TIMER";
        }
        return "";
    }

    @Override
    public String loadSettingStart(PlayState state, GameMode mode) {

        int startTimer = JSONManager.setting.getModeSetting(mode, SettingSave.TIMER, defaultValue);

        if (startTimer != 0) {
            state.getUiExtra().changeTimer(indexToTimer(startTimer), -1.0f);
        }

        RectangleMapObject end = new RectangleMapObject();
        end.setName("End");
        end.getProperties().put("text", endText);
        end.getProperties().put("triggeredId", "runOnGlobalTimerConclude");

        TiledObjectUtil.parseAddTiledEvent(state, end);

        return "";
    }

    /**
     * Convert timer from index in list to actual time amount
     */
    private float indexToTimer(int index) {
        return index * 60.0f;
    }
}
