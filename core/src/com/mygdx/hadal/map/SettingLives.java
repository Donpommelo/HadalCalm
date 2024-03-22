package com.mygdx.hadal.map;

import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.actors.Text;
import com.mygdx.hadal.actors.UIHub;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.managers.JSONManager;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.text.UIText;
import com.mygdx.hadal.users.Transition;
import com.mygdx.hadal.users.User;

import static com.mygdx.hadal.managers.SkinManager.SKIN;
import static com.mygdx.hadal.users.Transition.LONG_FADE_DELAY;

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
            String[] livesChoices = UIText.SETTING_LIVES_OPTIONS.text().split(",");
            Text lives = new Text(UIText.SETTING_LIVES.text());
            lives.setScale(UIHub.DETAILS_SCALE);

            livesOptions = new SelectBox<>(SKIN);
            livesOptions.setItems(livesChoices);
            livesOptions.setWidth(UIHub.OPTIONS_WIDTH);
            livesOptions.setSelectedIndex(JSONManager.setting.getModeSetting(mode, settingTag, defaultValue));

            table.add(lives);
            table.add(livesOptions).height(UIHub.DETAIL_HEIGHT).pad(UIHub.DETAIL_PAD).row();
        }
    }

    @Override
    public void saveSetting(PlayState state, GameMode mode) {
        if (livesChoice) {
            JSONManager.setting.setModeSetting(mode, settingTag, livesOptions.getSelectedIndex());
        }
    }

    @Override
    public String loadUIStart(PlayState state, GameMode mode) {
        int startLives = livesChoice ? JSONManager.setting.getModeSetting(mode, settingTag, defaultValue) : lockedLives;
        return startLives != 0 ? "LIVES" : "";
    }

    @Override
    public String loadSettingStart(PlayState state, GameMode mode) {
        int startLives = livesChoice ? JSONManager.setting.getModeSetting(mode, settingTag, defaultValue) : lockedLives;
        if (startLives != 0) {
            for (User user : HadalGame.usm.getUsers().values()) {
                user.getScoreManager().setLives(startLives);
                user.setScoreUpdated(true);
            }
            mode.setJoinMidGame(false);
        } else {
            unlimitedLives = true;
        }
        return "";
    }

    @Override
    public void processPlayerDeath(PlayState state, GameMode mode, Schmuck perp, Player vic, DamageSource source, DamageTag... tags) {

        //null check in case this is an "extra kill" to give summoner kill credit for a summon
        if (vic != null) {
            User user = vic.getUser();
            if (user != null) {

                if (unlimitedLives) {
                    user.getTransitionManager().beginTransition(state,
                            new Transition()
                                    .setNextState(PlayState.TransitionState.RESPAWN)
                                    .setFadeDelay(state.getRespawnTime(vic))
                                    .setForewarnTime(LONG_FADE_DELAY)
                                    .setSpawnForewarned(true));
                } else {
                    user.getScoreManager().setLives(user.getScoreManager().getLives() - 1);
                    if (user.getScoreManager().getLives() <= 0) {
                        mode.processPlayerLivesOut(state, vic);
                    } else {
                        user.getTransitionManager().beginTransition(state, new Transition()
                                .setNextState(PlayState.TransitionState.RESPAWN)
                                .setFadeDelay(state.getRespawnTime(vic))
                                .setForewarnTime(LONG_FADE_DELAY)
                                .setSpawnForewarned(true));
                    }
                }
            }
        }
    }
}
