package com.mygdx.hadal.map;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.actors.Text;
import com.mygdx.hadal.actors.UIHub;
import com.mygdx.hadal.bots.BotLoadoutProcessor;
import com.mygdx.hadal.bots.BotManager;
import com.mygdx.hadal.bots.BotPersonality;
import com.mygdx.hadal.bots.BotPersonality.BotDifficulty;
import com.mygdx.hadal.bots.RallyPoint;
import com.mygdx.hadal.equip.Loadout;
import com.mygdx.hadal.managers.JSONManager;
import com.mygdx.hadal.server.packets.Packets;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.text.NameGenerator;
import com.mygdx.hadal.text.TooltipManager;
import com.mygdx.hadal.text.UIText;
import com.mygdx.hadal.users.User;

import static com.mygdx.hadal.managers.SkinManager.SKIN;

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

    private SelectBox<String> botNumberOptions, botDifficultyOptions;

    private int lockedBots;
    private boolean botsChoice;

    public SettingBots() { botsChoice = true; }

    /**
     * This is used for modes with a set number of bots
     * atm, this is only used for the hub so bots are cleared from user list upon returning
     * @param lockedBots: number of bots
     */
    public SettingBots(int lockedBots) { this.lockedBots = lockedBots; }

    @Override
    public void setSetting(PlayState state, GameMode mode, Table table) {
        if (botsChoice) {
            String[] botNumberChoices = UIText.SETTING_BOT_NUMBER_OPTIONS.text().split(",");
            Text bots = new Text(UIText.SETTING_BOT_NUMBER.text());
            bots.setScale(UIHub.DETAILS_SCALE);
            TooltipManager.addTooltip(bots, UIText.SETTING_BOTS_NUMBER_DESC.text());

            botNumberOptions = new SelectBox<>(SKIN);
            botNumberOptions.setItems(botNumberChoices);
            botNumberOptions.setWidth(UIHub.OPTIONS_WIDTH);
            botNumberOptions.setSelectedIndex(JSONManager.setting.getModeSetting(mode, settingTag1, defaultValue));


            String[] botDifficultyChoices = UIText.SETTING_BOT_DIFFICULTY_OPTIONS.text().split(",");
            Text botDifficulty = new Text(UIText.SETTING_BOT_DIFFICULTY.text());
            botDifficulty.setScale(UIHub.DETAILS_SCALE);

            botDifficultyOptions = new SelectBox<>(SKIN);
            botDifficultyOptions.setItems(botDifficultyChoices);
            botDifficultyOptions.setWidth(UIHub.OPTIONS_WIDTH);
            botDifficultyOptions.setSelectedIndex(JSONManager.setting.getModeSetting(mode, settingTag2, defaultValue));

            //bot difficulty option is disabled with no bots
            botDifficultyOptions.setDisabled(botNumberOptions.getSelectedIndex() == 1);
            botNumberOptions.addListener(new ChangeListener() {

                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    botDifficultyOptions.setDisabled(botNumberOptions.getSelectedIndex() == 1);
                }
            });

            table.add(bots);
            table.add(botNumberOptions).height(UIHub.DETAIL_HEIGHT).pad(UIHub.DETAIL_PAD).row();
            table.add(botDifficulty);
            table.add(botDifficultyOptions).height(UIHub.DETAIL_HEIGHT).pad(UIHub.DETAIL_PAD).row();
        }
    }

    @Override
    public void saveSetting(PlayState state, GameMode mode) {
        if (botsChoice) {
            JSONManager.setting.setModeSetting(mode, settingTag1, botNumberOptions.getSelectedIndex());
            JSONManager.setting.setModeSetting(mode, settingTag2, botDifficultyOptions.getSelectedIndex());
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
        int botNumberIndex = botsChoice ? JSONManager.setting.getModeSetting(mode, settingTag1, defaultValue) : lockedBots;
        mode.setBotDifficulty(indexToBotDifficulty(JSONManager.setting.getModeSetting(mode, settingTag2, defaultValue)));

        Array<User> oldBots = new Array<>();

        //go through all existing bots and clear them from the user list
        for (User user : HadalGame.usm.getUsers().values()) {
            if (user.getConnID() < 0) {
                oldBots.add(user);
            }
        }

        //ensure that bots from last match are cleared
        for (User user : oldBots) {
            user.getHitboxFilter().setUsed(false);
            HadalGame.usm.getUsers().remove(user.getConnID());
            HadalGame.server.sendToAllTCP(new Packets.RemoveScore(user.getConnID()));
        }

        //reset next connId, then create each bot while incrementing connId to ensure each has a unique one.
        lastBotConnID = -2;

        int numBots = getNumBots(state, mode, botNumberIndex);
        for (int i = 0; i < numBots; i++) {
            HadalGame.usm.getUsers().put(lastBotConnID, createBotUser(state));
            lastBotConnID--;
        }

        //clear existing rally points to avoid memory leak
        for (RallyPoint point : BotManager.rallyPoints.values()) {
            point.getConnections().clear();
            point.getShortestPaths().clear();
        }
        BotManager.rallyPoints.clear();

        //if any bots are preset, initiate bot rally points, otherwise don't bother
        if (numBots > 0) {
            BotManager.initiateRallyPoints(state, state.getMap());
            BotManager.initiatePathfindingThreads();
        }
    }

    private static int getNumBots(PlayState state, GameMode mode, int botNumberIndex) {
        int numBots = 0;

        //index 0 indicates "default" bots; the number of bots varies with the stage size and player count
        if (botNumberIndex == 0) {
            int desiredPlayers = state.getLevel().getSize().getPreferredPlayers();

            //for team modes, we add additional bots to make teams even
            if (mode.isTeamDesignated()) {
                desiredPlayers += (mode.getTeamNum() - desiredPlayers % mode.getTeamNum());
            }

            int playerAmount = HadalGame.usm.getNumPlayers();
            if (playerAmount < desiredPlayers) {
                numBots = desiredPlayers - playerAmount;
            }
        } else {
            numBots = botNumberIndex - 1;
        }
        return numBots;
    }

    //this creates a single bot user; giving them a random name and initiating their score fields
    private User createBotUser(PlayState state) {
        String botName = NameGenerator.generateFirstLast(true);
        Loadout botLoadout = BotLoadoutProcessor.getBotLoadout(state);
        return new User(lastBotConnID, botName, botLoadout);
    }

    private static BotPersonality.BotDifficulty indexToBotDifficulty(int index) {
        return switch (index) {
            case 1 -> BotDifficulty.MEDIUM;
            case 2 -> BotDifficulty.HARD;
            default -> BotDifficulty.EASY;
        };
    }
}
