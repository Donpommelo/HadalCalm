package com.mygdx.hadal.map;

import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.mygdx.hadal.actors.Text;
import com.mygdx.hadal.actors.UIHub;
import com.mygdx.hadal.managers.JSONManager;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.text.TooltipManager;
import com.mygdx.hadal.text.UIText;

import static com.mygdx.hadal.managers.SkinManager.SKIN;

/**
 * This mode setting is used for modes where the host can designate a score cap
 * When a player or team reaches the score cap, they are the winner
 * @author Jignificant Jodardus
 */
public class SettingScoreCap extends ModeSetting {

    private SelectBox<String> scoreCapOptions;

    private int scoreCap;

    @Override
    public void setSetting(PlayState state, GameMode mode, Table table) {
        String[] scoreCapChoices = UIText.SETTING_SCORECAP_OPTIONS.text().split(",");
        Text scorecap = new Text(UIText.SETTING_SCORECAP.text());
        scorecap.setScale(UIHub.DETAILS_SCALE);
        TooltipManager.addTooltip(scorecap, UIText.SETTING_SCORECAP_DESC.text());

        scoreCapOptions = new SelectBox<>(SKIN);
        scoreCapOptions.setItems(scoreCapChoices);
        scoreCapOptions.setWidth(UIHub.OPTIONS_WIDTH);
        scoreCapOptions.setSelectedIndex(JSONManager.setting.getModeSetting(mode, SettingSave.SCORE_CAP));

        table.add(scorecap);
        table.add(scoreCapOptions).height(UIHub.DETAIL_HEIGHT).pad(UIHub.DETAIL_PAD).row();
    }

    @Override
    public void saveSetting(PlayState state, GameMode mode) {
        JSONManager.setting.setModeSetting(mode, SettingSave.SCORE_CAP, scoreCapOptions.getSelectedIndex());
    }

    @Override
    public String loadUIStart(PlayState state, GameMode mode) {
        int startScoreCap = JSONManager.setting.getModeSetting(mode, SettingSave.SCORE_CAP);
        if (startScoreCap != 0) {
            return UIText.SETTING_SCORECAP_UI.text(Integer.toString(startScoreCap));
        }
        return "";
    }

    @Override
    public void loadSettingMisc(PlayState state, GameMode mode) {
        scoreCap = JSONManager.setting.getModeSetting(mode, SettingSave.SCORE_CAP);
    }

    @Override
    public void processPlayerScoreChange(PlayState state, int newScore) {
        if (scoreCap > 0) {
            if (newScore >= scoreCap) {
                if (state.getGlobalTimer() != null) {
                    state.getGlobalTimer().getEventData().preActivate(null, null);
                }
            }
        }
    }
}
