package com.mygdx.hadal.map;

import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.actors.ModeSettingSelection;
import com.mygdx.hadal.actors.Text;
import com.mygdx.hadal.bots.BotManager;
import com.mygdx.hadal.bots.RallyPoint;
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

    private static final String settingTag = "bot_number";
    private static final Integer defaultValue = 0;
    private static final Integer defaultValueSinglePlayer = 1;

    private SelectBox<String> botNumberOptions;

    private int lockedBots;
    private boolean botsChoice;

    public SettingBots() { botsChoice = true; }

    public SettingBots(int lockedBots) { this.lockedBots = lockedBots; }

    @Override
    public void setSetting(PlayState state, GameMode mode, Table table) {
        if (botsChoice) {
            String[] botNumberChoices = HText.SETTING_BOT_NUMBER_OPTIONS.text().split(",");
            Text bots = new Text(HText.SETTING_BOT_NUMBER.text(), 0, 0, false);
            bots.setScale(ModeSettingSelection.detailsScale);

            botNumberOptions = new SelectBox<>(GameStateManager.getSkin());
            botNumberOptions.setItems(botNumberChoices);
            botNumberOptions.setWidth(ModeSettingSelection.optionsWidth);
            if (GameStateManager.currentMode.equals(GameStateManager.Mode.SINGLE)) {
                botNumberOptions.setSelectedIndex(state.getGsm().getSetting().getModeSetting(mode, settingTag, defaultValueSinglePlayer));
            } else {
                botNumberOptions.setSelectedIndex(state.getGsm().getSetting().getModeSetting(mode, settingTag, defaultValue));
            }

            table.add(bots);
            table.add(botNumberOptions).height(ModeSettingSelection.detailHeight).pad(ModeSettingSelection.detailPad).row();
        }
    }

    @Override
    public void saveSetting(PlayState state, GameMode mode) {
        if (botsChoice) {
            state.getGsm().getSetting().setModeSetting(mode, settingTag, botNumberOptions.getSelectedIndex());
        }
    }

    //this will be the connId of the next bot created.
    private static int lastBotConnID = -1;
    @Override
    public void loadSettingMisc(PlayState state, GameMode mode) {
        if (!state.isServer()) { return; }
        int botNumberIndex = botsChoice ? state.getGsm().getSetting().getModeSetting(mode, settingTag, defaultValue) : lockedBots;

        Array<User> oldBots = new Array<>();

        //go through all existing bots and clear them from the user list
        for (User user: HadalGame.server.getUsers().values()) {
            if (user.getScores().getConnID() < 0) {
                oldBots.add(user);
            }
        }
        for (User user: oldBots) {
            user.getHitBoxFilter().setUsed(false);
            HadalGame.server.getUsers().remove(user.getScores().getConnID());
            HadalGame.server.sendToAllTCP(new Packets.RemoveScore(user.getScores().getConnID()));
        }

        //reset next connId, then create each bot while incrementing connId to ensure each has a unique one.
        lastBotConnID = -1;

        for (int i = 0; i < botNumberIndex; i++) {
            HadalGame.server.getUsers().put(lastBotConnID, createBotUser());
            lastBotConnID--;
        }

        //clear existing rally points to avoid memory leak
        for (RallyPoint point: BotManager.rallyPoints.values()) {
            point.getConnections().clear();
            point.getShortestPaths().clear();
        }
        BotManager.rallyPoints.clear();

        //if any bots are preset, initiate bot rally points, otherwise don't bother
        if (botNumberIndex > 0) {
            BotManager.initiateRallyPoints(state.getMap());
        }
    }

    //this creates a single bot user; giving them a random name and initiating their score fields
    private User createBotUser() {
        String botName = NameGenerator.generateFirstLast(true);
        return new User(null, new SavedPlayerFields(botName, lastBotConnID), new SavedPlayerFieldsExtra());
    }
}
