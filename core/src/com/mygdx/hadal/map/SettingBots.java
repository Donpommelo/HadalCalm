package com.mygdx.hadal.map;

import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.actors.ModeSettingSelection;
import com.mygdx.hadal.actors.Text;
import com.mygdx.hadal.bots.BotManager;
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.server.SavedPlayerFields;
import com.mygdx.hadal.server.SavedPlayerFieldsExtra;
import com.mygdx.hadal.server.User;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.NameGenerator;

import java.util.ArrayList;

public class SettingBots extends ModeSetting {

    private static final String[] botNumberChoices = {"0", "1", "2", "3", "4", "5", "6", "7", "8"};
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
            Text bots = new Text("BOT NUMBER: ", 0, 0, false);
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

    private static int lastBotConnID = -1;
    @Override
    public void loadSettingMisc(PlayState state, GameMode mode) {
        if (!state.isServer()) { return; }
        int botNumberIndex = botsChoice ? state.getGsm().getSetting().getModeSetting(mode, settingTag, defaultValue) : lockedBots;

        ArrayList<User> oldBots = new ArrayList<>();

        for (User user: HadalGame.server.getUsers().values()) {
            if (user.getScores().getConnID() < 0) {
                oldBots.add(user);
            }
        }
        for (User user: oldBots) {
            user.getHitBoxFilter().setUsed(false);
            HadalGame.server.getUsers().remove(user.getScores().getConnID());
        }

        lastBotConnID = -1;

        for (int i = 0; i < botNumberIndex; i++) {
            HadalGame.server.getUsers().put(lastBotConnID, createBotUser());
            lastBotConnID--;
        }

        if (botNumberIndex > 0) {
            BotManager.initiateRallyPoints(state.getMap());
        }
    }

    private User createBotUser() {
        String botName = NameGenerator.generateFirstLast(true);
        return new User(null, new SavedPlayerFields(botName, lastBotConnID), new SavedPlayerFieldsExtra());
    }
}
