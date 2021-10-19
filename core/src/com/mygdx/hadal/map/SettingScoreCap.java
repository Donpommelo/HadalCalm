package com.mygdx.hadal.map;

import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.mygdx.hadal.actors.ModeSettingSelection;
import com.mygdx.hadal.actors.Text;
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.states.PlayState;

/**
 * This mode setting is used for modes where the host can designate a score cap
 * When a player or team reaches the score cap, they are the winner
 * @author Jignificant Jodardus
 */
public class SettingScoreCap extends ModeSetting {

    public static final String[] scoreCapChoices = {"UNLIMITED", "1 POINT", "2 POINTS", "3 POINTS", "4 POINTS", "5 POINTS", "6 POINTS",
        "7 POINTS", "8 POINTS", "9 POINTS", "10 POINTS", "11 POINTS", "12 POINTS", "13 POINTS", "14 POINTS", "15 POINTS",
        "16 POINTS", "17 POINTS", "18 POINTS"};
    public static final String settingTag = "score_cap";
    public static final Integer defaultValue = 0;

    private SelectBox<String> scoreCapOptions;

    private int scoreCap;

    @Override
    public void setSetting(PlayState state, GameMode mode, Table table) {
        
        Text scorecap = new Text("SCORE WIN CONDITION: ", 0, 0, false);
        scorecap.setScale(ModeSettingSelection.detailsScale);

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
            return "FIRST TO " + startScoreCap + " POINT(S) WINS";
        }
        return "";
    }

    @Override
    public void loadSettingMisc(PlayState state, GameMode mode) {
        scoreCap = state.getGsm().getSetting().getModeSetting(mode, settingTag, defaultValue);
    }

    @Override
    public void processPlayerScoreChange(PlayState state, GameMode mode, Player p, int newScore) {
        if (scoreCap > 0) {
            if (newScore >= scoreCap) {
                if (state.getGlobalTimer() != null) {
                    state.getGlobalTimer().getEventData().preActivate(null, null);
                }
            }
        }
    }
}
