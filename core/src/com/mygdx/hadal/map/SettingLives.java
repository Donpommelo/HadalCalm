package com.mygdx.hadal.map;

import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.actors.ModeSettingSelection;
import com.mygdx.hadal.actors.Text;
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.server.User;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.text.HText;

import static com.mygdx.hadal.states.PlayState.defaultFadeOutSpeed;

/**
 * This mode setting is used for modes where the host can designate a number of lives.
 * @author Jergarita Jisrael
 */
public class SettingLives extends ModeSetting {

    public static final String settingTag = "lives";
    public static final Integer defaultValue = 0;

    private SelectBox<String> livesOptions;

    private int lockedLives;
    private boolean livesChoice;

    private boolean unlimitedLives;

    public SettingLives() { livesChoice = true; }

    public SettingLives(int lockedLives) { this.lockedLives = lockedLives; }

    @Override
    public void setSetting(PlayState state, GameMode mode, Table table) {
        if (livesChoice) {
            String[] livesChoices = HText.SETTING_LIVES_OPTIONS.text().split(",");
            Text lives = new Text(HText.SETTING_LIVES.text());
            lives.setScale(ModeSettingSelection.detailsScale);

            livesOptions = new SelectBox<>(GameStateManager.getSkin());
            livesOptions.setItems(livesChoices);
            livesOptions.setWidth(ModeSettingSelection.optionsWidth);
            livesOptions.setSelectedIndex(state.getGsm().getSetting().getModeSetting(mode, settingTag, defaultValue));

            table.add(lives);
            table.add(livesOptions).height(ModeSettingSelection.detailHeight).pad(ModeSettingSelection.detailPad).row();
        }
    }

    @Override
    public void saveSetting(PlayState state, GameMode mode) {
        if (livesChoice) {
            state.getGsm().getSetting().setModeSetting(mode, settingTag, livesOptions.getSelectedIndex());
        }
    }

    @Override
    public String loadUIStart(PlayState state, GameMode mode) {
        int startLives = livesChoice ? state.getGsm().getSetting().getModeSetting(mode, settingTag, defaultValue) : lockedLives;
        return startLives != 0 ? "LIVES" : "";
    }

    @Override
    public String loadSettingStart(PlayState state, GameMode mode) {
        int startLives = livesChoice ? state.getGsm().getSetting().getModeSetting(mode, settingTag, defaultValue) : lockedLives;

        if (startLives != 0) {
            for (User user: HadalGame.server.getUsers().values()) {
                user.getScores().setLives(startLives);
                user.setScoreUpdated(true);
            }
            mode.setJoinMidGame(false);
        } else {
            unlimitedLives = true;
        }
        return "";
    }

    @Override
    public void processPlayerDeath(PlayState state, GameMode mode, Schmuck perp, Player vic, DamageTypes... tags) {

        //null check in case this is an "extra kill" to give summoner kill credit for a summon
        if (vic != null) {
            User user = vic.getUser();
            if (user != null) {
                if (unlimitedLives) {
                    user.beginTransition(state, PlayState.TransitionState.RESPAWN, false, defaultFadeOutSpeed, state.getRespawnTime());
                } else {
                    user.getScores().setLives(user.getScores().getLives() - 1);
                    if (user.getScores().getLives() <= 0) {
                        mode.processPlayerLivesOut(state, vic);
                    } else {
                        user.beginTransition(state, PlayState.TransitionState.RESPAWN, false, defaultFadeOutSpeed, state.getRespawnTime());
                    }
                }
            }
        }
    }
}
