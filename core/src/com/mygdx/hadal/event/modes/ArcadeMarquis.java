package com.mygdx.hadal.event.modes;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.StringBuilder;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.constants.BodyConstants;
import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.input.PlayerAction;
import com.mygdx.hadal.server.util.PacketManager;
import com.mygdx.hadal.map.ArcadeMode;
import com.mygdx.hadal.map.GameMode;
import com.mygdx.hadal.save.UnlockLevel;
import com.mygdx.hadal.save.UnlockManager;
import com.mygdx.hadal.server.packets.Packets;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.text.UIText;
import com.mygdx.hadal.users.User;
import com.mygdx.hadal.utils.b2d.HadalBody;

import java.util.HashMap;
import java.util.Map;

import static com.mygdx.hadal.managers.SkinManager.FONT_UI;

/**
 * The Arcade Marquis appears in the Arcade Break room.
 * ATM, this displays the next mode options and allows the players to vote.
 *
 */
public class ArcadeMarquis extends Event {

    private static final int CHOICE_NUMBER = 4;
    private static final float CHOICE_SCALE = 0.4f;

    private static final Array<ArcadeMode> modeChoices = new Array<>();
    private static final Array<UnlockLevel> mapChoices = new Array<>();

    public ArcadeMarquis(PlayState state, Vector2 startPos, Vector2 size) {
        super(state, startPos, size);

        if (state.isServer()) {
            initializeChoices(state);
        }
    }

    @Override
    public void create() {
        this.eventData = new EventData(this);
        this.body = new HadalBody(eventData, startPos, size, BodyConstants.BIT_SENSOR, (short) 0, (short) 0)
                .setBodyType(BodyDef.BodyType.KinematicBody)
                .addToWorld(world);
    }

    private static final StringBuilder text = new StringBuilder();
    @Override
    public void render(SpriteBatch batch, Vector2 entityLocation) {
        batch.setProjectionMatrix(state.getCamera().combined);
        FONT_UI.getData().setScale(CHOICE_SCALE);
        FONT_UI.draw(batch, text.toString(), entityLocation.x, entityLocation.y);
    }

    /**
     * Determine what the next mode options are
     */
    private static void initializeChoices(PlayState state) {
        modeChoices.clear();
        mapChoices.clear();

        //first option will always be a standard deathmatch
        modeChoices.add(ArcadeMode.DEATHMATCH);
        mapChoices.add(getApplicableMap(GameMode.DEATHMATCH));

        while (modeChoices.size < CHOICE_NUMBER) {
            int index = MathUtils.random(ArcadeMode.values().length - 1);
            ArcadeMode randomMode = ArcadeMode.values()[index];

            if (!modeChoices.contains(randomMode, false) || randomMode.equals(ArcadeMode.DEATHMATCH)) {
                modeChoices.add(randomMode);
                mapChoices.add(getApplicableMap(randomMode.getMode()));
            }
        }

        updateText();

        String[] modeNames = new String[modeChoices.size];
        String[] mapNames = new String[mapChoices.size];
        for (int i = 0; i < modeChoices.size; i++) {
            modeNames[i] = modeChoices.get(i).name();
            mapNames[i] = mapChoices.get(i).name();
        }

        PacketManager.serverTCPAll(new Packets.SyncArcadeModeChoices(modeNames, mapNames));
    }

    private static final HashMap<Integer, Integer> voteCounts = new HashMap<>();

    /**
     * When moving on to the next round, we determine the winner of the vote and return its index
     * Ties are broken randomly
     */
    public static int getVotedOption() {
        int maxVote = 0;
        for (int i = 0; i < modeChoices.size; i++) {
            int voteCount = 0;
            for (User user : HadalGame.usm.getUsers().values()) {
                if (i == user.getScoreManager().getNextRoundVote()) {
                    voteCount++;
                }
            }
            voteCounts.put(i, voteCount);
            if (voteCount > maxVote) {
                maxVote = voteCount;
            }
        }

        Array<Integer> winningOptions = new Array<>();
        for (Map.Entry<Integer, Integer> entry : voteCounts.entrySet()) {
            if (entry.getValue() == maxVote) {
                winningOptions.add(entry.getKey());
            }
        }

        return winningOptions.random();
    }

    /**
     * This is run by the client receiving options from the server.
     */
    public static void updateClientChoices(String[] modes, String[] maps) {
        modeChoices.clear();
        mapChoices.clear();

        for (int i = 0; i < modes.length; i++) {
            modeChoices.add(ArcadeMode.getByName(modes[i]));
            mapChoices.add(UnlockLevel.getByName(maps[i]));
        }

        updateText();
    }

    /**
     * This is run when a player votes. Server keeps track and client sends their choice.
     */
    public static void playerVote(PlayState state, User user, int vote) {
        if (state.isServer()) {
            user.getScoreManager().setNextRoundVote(vote);
        } else {
            PacketManager.clientTCP(new Packets.SyncClientModeVote(vote));
        }
    }

    private static UnlockLevel getApplicableMap(GameMode mode) {
        return UnlockLevel.getRandomLevelForMode(mode, new Array<>(new UnlockManager.UnlockTag[]{UnlockManager.UnlockTag.CURATED}));
    }

    private static void updateText() {
        text.clear();
        text.append(UIText.UI_NUMKEYS_TO_VOTE.text(
                PlayerAction.SWITCH_TO_1.getKeyText(),
                PlayerAction.SWITCH_TO_2.getKeyText(),
                PlayerAction.SWITCH_TO_3.getKeyText(),
                PlayerAction.SWITCH_TO_4.getKeyText())).append("\n");
        text.append(UIText.UI_ENTER_TO_READY.text(PlayerAction.READY_UP.getKeyText())).append("\n\n");
        for (int i = 0; i < modeChoices.size; i++) {
            text.append("(").append(i + 1).append(") ")
                    .append(modeChoices.get(i).getName()).append(": ")
                    .append(mapChoices.get(i).getName()).append("\n");
        }
    }

    public static Array<ArcadeMode> getModeChoices() { return modeChoices; }

    public static Array<UnlockLevel> getMapChoices() { return mapChoices; }
}
