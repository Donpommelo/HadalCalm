package com.mygdx.hadal.map;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.actors.ModeSettingSelection;
import com.mygdx.hadal.actors.Text;
import com.mygdx.hadal.bots.BotManager;
import com.mygdx.hadal.bots.BotPersonality;
import com.mygdx.hadal.bots.RallyPoint;
import com.mygdx.hadal.bots.BotPersonality.BotDifficulty;
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.server.SavedPlayerFields;
import com.mygdx.hadal.server.SavedPlayerFieldsExtra;
import com.mygdx.hadal.server.User;
import com.mygdx.hadal.server.packets.Packets;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.text.HText;
import com.mygdx.hadal.text.NameGenerator;

/**
 * This mode setting sets the number of bots in the game
 * If an int is provided as an input, the number of bots is locked to that amount.
 * Otherwise, this presents a ui element that allows the player to choose the number of bots
 * @author Picycle Proderf
 */
public class SettingBots extends ModeSetting {

    private static final String settingTag1 = "bot_number";
    private static final Integer defaultValue = 0;
    private static final String settingTag2 = "bot_difficulty";
    private static final Integer defaultValueSinglePlayer = 1;

    private SelectBox<String> botNumberOptions, botDifficultyOptions;

    private int lockedBots;
    private boolean botsChoice;

    public SettingBots() { botsChoice = true; }

    public SettingBots(int lockedBots) { this.lockedBots = lockedBots; }

    @Override
    public void setSetting(PlayState state, GameMode mode, Table table) {
        if (botsChoice) {
            String[] botNumberChoices = HText.SETTING_BOT_NUMBER_OPTIONS.text().split(",");
            Text bots = new Text(HText.SETTING_BOT_NUMBER.text());
            bots.setScale(ModeSettingSelection.detailsScale);

            botNumberOptions = new SelectBox<>(GameStateManager.getSkin());
            botNumberOptions.setItems(botNumberChoices);
            botNumberOptions.setWidth(ModeSettingSelection.optionsWidth);
            if (GameStateManager.currentMode.equals(GameStateManager.Mode.SINGLE)) {
                botNumberOptions.setSelectedIndex(state.getGsm().getSetting().getModeSetting(mode, settingTag1, defaultValueSinglePlayer));
            } else {
                botNumberOptions.setSelectedIndex(state.getGsm().getSetting().getModeSetting(mode, settingTag1, defaultValue));
            }

            String[] botDifficultyChoices = HText.SETTING_BOT_DIFFICULTY_OPTIONS.text().split(",");
            Text botDifficulty = new Text(HText.SETTING_BOT_DIFFICULTY.text());
            botDifficulty.setScale(ModeSettingSelection.detailsScale);

            botDifficultyOptions = new SelectBox<>(GameStateManager.getSkin());
            botDifficultyOptions.setItems(botDifficultyChoices);
            botDifficultyOptions.setWidth(ModeSettingSelection.optionsWidth);
            botDifficultyOptions.setSelectedIndex(state.getGsm().getSetting().getModeSetting(mode, settingTag2, defaultValue));

            //bot difficulty option is disabled with no bots
            botDifficultyOptions.setDisabled(botNumberOptions.getSelectedIndex() == 0);
            botNumberOptions.addListener(new ChangeListener() {

                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    botDifficultyOptions.setDisabled(botNumberOptions.getSelectedIndex() == 0);
                }
            });

            table.add(bots);
            table.add(botNumberOptions).height(ModeSettingSelection.detailHeight).pad(ModeSettingSelection.detailPad).row();
            table.add(botDifficulty);
            table.add(botDifficultyOptions).height(ModeSettingSelection.detailHeight).pad(ModeSettingSelection.detailPad).row();
        }
    }

    @Override
    public void saveSetting(PlayState state, GameMode mode) {
        if (botsChoice) {
            state.getGsm().getSetting().setModeSetting(mode, settingTag1, botNumberOptions.getSelectedIndex());
            state.getGsm().getSetting().setModeSetting(mode, settingTag2, botDifficultyOptions.getSelectedIndex());
        }
    }

    @Override
    public void processGameEnd() {
        BotManager.terminatePathfindingThreads();
    }

    //this will be the connId of the next bot created. -2, b/c some things treat "-1" as indicating a null player
    private static int lastBotConnID = -2;
    @Override
    public void loadSettingMisc(PlayState state, GameMode mode) {
        if (!state.isServer()) { return; }
        int botNumberIndex = botsChoice ? state.getGsm().getSetting().getModeSetting(mode, settingTag1, defaultValue) : lockedBots;
        mode.setBotDifficulty(indexToBotDifficulty(state.getGsm().getSetting().getModeSetting(mode, settingTag2, defaultValue)));

        Array<User> oldBots = new Array<>();

        //go through all existing bots and clear them from the user list
        for (User user : HadalGame.server.getUsers().values()) {
            if (user.getScores().getConnID() < 0) {
                oldBots.add(user);
            }
        }

        //ensure that bots from last match are cleared
        for (User user : oldBots) {
            user.getHitBoxFilter().setUsed(false);
            HadalGame.server.getUsers().remove(user.getScores().getConnID());
            HadalGame.server.sendToAllTCP(new Packets.RemoveScore(user.getScores().getConnID()));
        }

        //reset next connId, then create each bot while incrementing connId to ensure each has a unique one.
        lastBotConnID = -2;

        for (int i = 0; i < botNumberIndex; i++) {
            HadalGame.server.getUsers().put(lastBotConnID, createBotUser());
            lastBotConnID--;
        }

        //clear existing rally points to avoid memory leak
        for (RallyPoint point : BotManager.rallyPoints.values()) {
            point.getConnections().clear();
            point.getShortestPaths().clear();
        }
        BotManager.rallyPoints.clear();

        //if any bots are preset, initiate bot rally points, otherwise don't bother
        if (botNumberIndex > 0) {
            BotManager.initiateRallyPoints(state, state.getMap());
            BotManager.initiatePathfindingThreads();
        }
    }

    //this creates a single bot user; giving them a random name and initiating their score fields
    private User createBotUser() {
        String botName = NameGenerator.generateFirstLast(true);
        return new User(null, new SavedPlayerFields(botName, lastBotConnID), new SavedPlayerFieldsExtra());
    }

    private static BotPersonality.BotDifficulty indexToBotDifficulty(int index) {
        return switch (index) {
            case 1 -> BotDifficulty.MEDIUM;
            case 2 -> BotDifficulty.HARD;
            default -> BotDifficulty.EASY;
        };
    }
}
