package com.mygdx.hadal.map;

import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.actors.Text;
import com.mygdx.hadal.actors.UIHub;
import com.mygdx.hadal.actors.UITag;
import com.mygdx.hadal.equip.Loadout;
import com.mygdx.hadal.event.modes.ArcadeMarquis;
import com.mygdx.hadal.managers.JSONManager;
import com.mygdx.hadal.managers.PacketManager;
import com.mygdx.hadal.managers.TransitionManager.TransitionState;
import com.mygdx.hadal.save.SavedLoadout;
import com.mygdx.hadal.save.UnlockEquip;
import com.mygdx.hadal.save.UnlockLevel;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.server.packets.Packets;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.states.ResultsState;
import com.mygdx.hadal.statuses.Celebrating;
import com.mygdx.hadal.text.UIText;
import com.mygdx.hadal.users.ScoreManager;
import com.mygdx.hadal.users.Transition;
import com.mygdx.hadal.users.User;
import com.mygdx.hadal.utils.TiledObjectUtil;

import static com.mygdx.hadal.managers.SkinManager.SKIN;
import static com.mygdx.hadal.users.Transition.*;

/**
 * This mode setting is used for modes where the host can designate a time limit.
 * When the time expires, the player or team with the most points wins
 * @author Blashutanga Bluryl
 */
public class SettingArcade extends ModeSetting {

    private static final UnlockEquip[] BASE_EQUIP = {UnlockEquip.SPEARGUN, UnlockEquip.NOTHING, UnlockEquip.NOTHING};

    public static boolean arcade, overtime;
    public static int roundNum, currentRound, winCap;
    public static ArcadeMode currentMode;

    private final String endText;

    private SelectBox<String> timerOptions, roundCountOptions, startingScrapOptions, roundScrapOptions, winCountOptions;

    public SettingArcade() {
        this.endText = ResultsState.MAGIC_WORD;
    }

    @Override
    public void setSetting(PlayState state, GameMode mode, Table table) {
        String[] timerChoices = UIText.SETTING_ARCADE_BREAK_TIMER_OPTIONS.text().split(",");
        Text timer = new Text(UIText.SETTING_ARCADE_BREAK_TIMER.text());
        timer.setScale(UIHub.DETAILS_SCALE);

        timerOptions = new SelectBox<>(SKIN);
        timerOptions.setItems(timerChoices);
        timerOptions.setWidth(UIHub.OPTIONS_WIDTH);
        timerOptions.setSelectedIndex(JSONManager.setting.getModeSetting(mode, SettingSave.ARCADE_BREAK_TIME));

        table.add(timer);
        table.add(timerOptions).height(UIHub.DETAIL_HEIGHT).pad(UIHub.DETAIL_PAD).row();

        String[] roundChoices = UIText.SETTING_ARCADE_ROUNDS_OPTIONS.text().split(",");
        Text rounds = new Text(UIText.SETTING_ARCADE_ROUNDS.text());
        rounds.setScale(UIHub.DETAILS_SCALE);

        roundCountOptions = new SelectBox<>(SKIN);
        roundCountOptions.setItems(roundChoices);
        roundCountOptions.setWidth(UIHub.OPTIONS_WIDTH);
        roundCountOptions.setSelectedIndex(JSONManager.setting.getModeSetting(mode, SettingSave.ARCADE_ROUND_NUMBER));

        table.add(rounds);
        table.add(roundCountOptions).height(UIHub.DETAIL_HEIGHT).pad(UIHub.DETAIL_PAD).row();

        String[] winChoices = UIText.SETTING_ARCADE_WIN_CAP_OPTIONS.text().split(",");
        Text wins = new Text(UIText.SETTING_ARCADE_WIN_CAP.text());
        wins.setScale(UIHub.DETAILS_SCALE);

        winCountOptions = new SelectBox<>(SKIN);
        winCountOptions.setItems(winChoices);
        winCountOptions.setWidth(UIHub.OPTIONS_WIDTH);
        winCountOptions.setSelectedIndex(JSONManager.setting.getModeSetting(mode, SettingSave.ARCADE_SCORE_CAP));

        table.add(wins);
        table.add(winCountOptions).height(UIHub.DETAIL_HEIGHT).pad(UIHub.DETAIL_PAD).row();

        String[] currencyChoices = UIText.SETTING_ARCADE_STARTING_SCRAP_OPTIONS.text().split(",");
        Text currency = new Text(UIText.SETTING_ARCADE_STARTING_SCRAP.text());
        currency.setScale(UIHub.DETAILS_SCALE);

        startingScrapOptions = new SelectBox<>(SKIN);
        startingScrapOptions.setItems(currencyChoices);
        startingScrapOptions.setWidth(UIHub.OPTIONS_WIDTH);
        startingScrapOptions.setSelectedIndex(JSONManager.setting.getModeSetting(mode, SettingSave.ARCADE_CURRENCY_START));

        table.add(currency);
        table.add(startingScrapOptions).height(UIHub.DETAIL_HEIGHT).pad(UIHub.DETAIL_PAD).row();

        String[] currencyGainChoices = UIText.SETTING_ARCADE_BASE_SCRAP_OPTIONS.text().split(",");
        Text currencyGain = new Text(UIText.SETTING_ARCADE_BASE_SCRAP.text());
        currencyGain.setScale(UIHub.DETAILS_SCALE);

        roundScrapOptions = new SelectBox<>(SKIN);
        roundScrapOptions.setItems(currencyGainChoices);
        roundScrapOptions.setWidth(UIHub.OPTIONS_WIDTH);
        roundScrapOptions.setSelectedIndex(JSONManager.setting.getModeSetting(mode, SettingSave.ARCADE_CURRENCY_ROUND));

        table.add(currencyGain);
        table.add(roundScrapOptions).height(UIHub.DETAIL_HEIGHT).pad(UIHub.DETAIL_PAD).row();
    }

    @Override
    public void saveSetting(PlayState state, GameMode mode) {
        JSONManager.setting.setModeSetting(mode, SettingSave.ARCADE_BREAK_TIME, timerOptions.getSelectedIndex());
        JSONManager.setting.setModeSetting(mode, SettingSave.ARCADE_ROUND_NUMBER, roundCountOptions.getSelectedIndex());
        JSONManager.setting.setModeSetting(mode, SettingSave.ARCADE_SCORE_CAP, winCountOptions.getSelectedIndex());
        JSONManager.setting.setModeSetting(mode, SettingSave.ARCADE_CURRENCY_START, startingScrapOptions.getSelectedIndex());
        JSONManager.setting.setModeSetting(mode, SettingSave.ARCADE_CURRENCY_ROUND, roundScrapOptions.getSelectedIndex());
    }

    @Override
    public String loadUIStart(PlayState state, GameMode mode) {
        int startTimer = JSONManager.setting.getModeSetting(mode, SettingSave.ARCADE_BREAK_TIME);
        if (startTimer != 0) {
            return "TIMER";
        }
        return "";
    }

    @Override
    public String loadSettingStart(PlayState state, GameMode mode) {
        if (!arcade) {
            startArcade(mode);
        } else {
            currentRound++;
        }

        for (User user : HadalGame.usm.getUsers().values()) {
            user.setScoreUpdated(true);
        }

        int startTimer = JSONManager.setting.getModeSetting(mode, SettingSave.ARCADE_BREAK_TIME);

        if (startTimer != 0) {
            state.getTimerManager().changeTimer(indexToTimer(startTimer), -1.0f);
        }

        RectangleMapObject end = new RectangleMapObject();
        end.setName("End");
        end.getProperties().put("text", endText);
        end.getProperties().put("triggeredId", "runOnGlobalTimerConclude");
        end.getProperties().put("incrementWins", false);

        TiledObjectUtil.parseAddTiledEvent(state, end);

        return "";
    }

    @Override
    public void loadSettingMisc(PlayState state, GameMode mode) {
        arcade = true;
        for (User user : HadalGame.usm.getUsers().values()) {
            user.getScoreManager().setReady(false);
        }
    }

    @Override
    public void postCreatePlayer(PlayState state, GameMode mode, Player p) {
        if (state.isServer() && p.getUser().getScoreManager().isWonLast()) {
            p.getPlayerData().addStatus(new Celebrating(state, p.getPlayerData(), p.getPlayerData()));
        }
    }

    @Override
    public void processNewPlayerLoadout(PlayState state, GameMode mode, Loadout newLoadout, int connID) {
        for (int i = 0; i < Loadout.MAX_WEAPON_SLOTS; i++) {
            if (BASE_EQUIP.length > i) {
                newLoadout.multitools[i] = BASE_EQUIP[i];
            }
        }
    }

    private void startArcade(GameMode mode) {
        for (User user : HadalGame.usm.getUsers().values()) {
            user.getScoreManager().setNextRoundVote(-1);
            user.getLoadoutManager().setArcadeLoadout(new Loadout(SavedLoadout.createNewLoadout()));

            user.getScoreManager().setWins(0);
            user.getScoreManager().setCurrency(
                    indexToStartingCurrency(JSONManager.setting.getModeSetting(mode, SettingSave.ARCADE_CURRENCY_START)));
        }

        roundNum = indexToRoundNum(JSONManager.setting.getModeSetting(mode, SettingSave.ARCADE_ROUND_NUMBER));
        winCap = indexToRoundNum(JSONManager.setting.getModeSetting(mode, SettingSave.ARCADE_SCORE_CAP));
        currentRound = 0;
        overtime = false;
    }

    public static void processEndOfRound(PlayState state, GameMode mode) {
        for (User user : HadalGame.usm.getUsers().values()) {
            user.setScoreUpdated(true);
        }

        if (mode.equals(GameMode.ARCADE)) {
            int vote = ArcadeMarquis.getVotedOption();
            currentMode = ArcadeMarquis.getModeChoices().get(vote);

            state.getTransitionManager().setNextLevel(ArcadeMarquis.getMapChoices().get(vote));
            state.getTransitionManager().setNextMode(currentMode.getMode());
        } else {
            if (roundNum != 0 && currentRound > roundNum && endArcadeMode(state)) {
                return;
            } else {
                boolean winCapReached = false;
                for (User user : HadalGame.usm.getUsers().values()) {
                    if (user.getScoreManager().getWins() >= winCap) {
                        winCapReached = true;
                        break;
                    }
                }
                if (winCap != 0 && winCapReached && endArcadeMode(state)) {
                    return;
                } else {
                    state.getTransitionManager().setNextLevel(UnlockLevel.HUB_BREAK);
                    state.getTransitionManager().setNextMode(GameMode.ARCADE);
                    for (User user : HadalGame.usm.getUsers().values()) {
                        user.getScoreManager().setCurrency(user.getScoreManager().getCurrency() +
                                indexToRoundCurrency(JSONManager.setting.getModeSetting(mode, SettingSave.ARCADE_CURRENCY_ROUND)));
                    }
                }
            }
        }

        for (User user : HadalGame.usm.getUsers().values()) {
            user.getTransitionManager().beginTransition(state, new Transition()
                    .setNextState(TransitionState.NEWLEVEL)
                    .setFadeSpeed(SLOW_FADE_OUT_SPEED)
                    .setFadeDelay(MEDIUM_FADE_DELAY)
                    .setOverride(true));
        }
    }

    public static void readyUp(PlayState state, int playerID) {
        User readyUser = HadalGame.usm.getUsers().get(playerID);
        if (state.isServer()) {
            if (readyUser != null && !readyUser.isSpectator()) {
                readyUser.getScoreManager().setReady(true);
                PacketManager.serverTCPAll(state, new Packets.ClientReady(playerID));
            }
        } else {
            readyUser.getScoreManager().setReady(true);
        }

        boolean reddy = true;
        for (User user : HadalGame.usm.getUsers().values()) {
            if (!user.isSpectator() && !user.getScoreManager().isReady()) {
                reddy = false;
                break;
            }
        }
        if (reddy && state.isServer()) {
            int vote = ArcadeMarquis.getVotedOption();
            currentMode = ArcadeMarquis.getModeChoices().get(vote);
            state.getTransitionManager().loadLevel(ArcadeMarquis.getMapChoices().get(vote), currentMode.getMode(),
                    TransitionState.NEWLEVEL, "");
        }

        state.getUIManager().getUiExtra().syncUIText(UITag.uiType.WINBOARD);
    }

    public static void addNewUser(ScoreManager score) {
        int baseCurrency = indexToStartingCurrency(JSONManager.setting.getModeSetting(GameMode.ARCADE, SettingSave.ARCADE_CURRENCY_START));
        baseCurrency += (indexToRoundCurrency(JSONManager.setting.getModeSetting(GameMode.ARCADE, SettingSave.ARCADE_CURRENCY_ROUND))
                * currentRound);
        score.setCurrency(baseCurrency);
    }

    private static boolean endArcadeMode(PlayState state) {
        int highScore = 0;
        int numHighScore = 0;
        for (User user : HadalGame.usm.getUsers().values()) {
            if (user.getScoreManager().getWins() == highScore) {
                numHighScore++;
            } else if (user.getScoreManager().getWins() > highScore) {
                highScore = user.getScoreManager().getWins();
                numHighScore = 1;
            }
        }

        if (numHighScore == 1) {
            arcade = false;

            for (User user : HadalGame.usm.getUsers().values()) {
                user.getScoreManager().setScore(user.getScoreManager().getWins());
            }

            //need to set this boolean so the state can end, since levelEnd() was called to process arcade ending
            state.getEndgameManager().setLevelEnded(false);
            state.getEndgameManager().levelEnd(ResultsState.MAGIC_WORD, false, true, DEFAULT_FADE_DELAY);
            return true;
        }
        overtime = true;
        return false;
    }

    /**
     * Convert timer from index in list to actual time amount
     */
    private float indexToTimer(int index) {
        return index * 60.0f;
    }

    private int indexToRoundNum(int index) {
        return switch (index) {
            case 1 -> 3;
            case 2 -> 4;
            case 3 -> 5;
            case 4 -> 6;
            case 5 -> 7;
            case 6 -> 8;
            case 7 -> 9;
            case 8 -> 10;
            default ->  0;
        };
    }

    private static int indexToStartingCurrency(int index) {
        return switch (index) {
            case 1 -> 10;
            case 2 -> 15;
            case 3 -> 20;
            case 4 -> 25;
            case 5 -> 30;
            default ->  0;
        };
    }

    private static int indexToRoundCurrency(int index) {
        return switch (index) {
            case 1 -> 6;
            case 2 -> 8;
            case 3 -> 10;
            case 4 -> 12;
            case 5 -> 14;
            default ->  4;
        };
    }
}
