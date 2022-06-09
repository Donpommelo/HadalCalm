package com.mygdx.hadal.map;

import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.mygdx.hadal.actors.ModeSettingSelection;
import com.mygdx.hadal.actors.Text;
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.text.UIText;
import com.mygdx.hadal.text.TooltipManager;

/**
 * This mode setting is used for modes where the host can designate a score cap
 * When a player or team reaches the score cap, they are the winner
 * @author Jignificant Jodardus
 */
public class SettingTeamScoreCap extends ModeSetting {

    public static final String settingTag = "team_score_cap";
    public static final Integer defaultValue = 0;

    private SelectBox<String> scoreCapOptions;

    private int teamScoreCap;

    @Override
    public void setSetting(PlayState state, GameMode mode, Table table) {
        String[] scoreCapChoices = UIText.SETTING_SCORECAP_OPTIONS.text().split(",");
        Text scorecap = new Text(UIText.SETTING_SCORECAP.text());
        scorecap.setScale(ModeSettingSelection.detailsScale);
        TooltipManager.addTooltip(scorecap, UIText.SETTING_SCORECAP_TEAM_DESC.text());

        scoreCapOptions = new SelectBox<>(GameStateManager.getSkin());
        scoreCapOptions.setItems(scoreCapChoices);
        scoreCapOptions.setWidth(ModeSettingSelection.optionsWidth);
        scoreCapOptions.setSelectedIndex(state.getGsm().getSetting().getModeSetting(mode, settingTag, defaultValue));

        table.add(scorecap);
        table.add(scoreCapOptions).height(ModeSettingSelection.detailHeight).pad(ModeSettingSelection.detailPad).row();
    }

    @Override
    public void saveSetting(PlayState state, GameMode mode) {
        state.getGsm().getSetting().setModeSetting(mode, settingTag, scoreCapOptions.getSelectedIndex());
    }

    @Override
    public String loadUIStart(PlayState state, GameMode mode) {
        int startScoreCap = state.getGsm().getSetting().getModeSetting(mode, settingTag, defaultValue);
        if (startScoreCap != 0) {
            return UIText.SETTING_SCORECAP_UI.text(Integer.toString(startScoreCap));
        }
        return "";
    }

    @Override
    public void loadSettingMisc(PlayState state, GameMode mode) {
        teamScoreCap = state.getGsm().getSetting().getModeSetting(mode, settingTag, defaultValue);
    }

    @Override
    public void processTeamScoreChange(PlayState state, int newScore) {
        if (teamScoreCap > 0) {
            if (newScore >= teamScoreCap) {
                if (state.getGlobalTimer() != null) {
                    state.getGlobalTimer().getEventData().preActivate(null, null);
                }
            }
        }
    }
}
