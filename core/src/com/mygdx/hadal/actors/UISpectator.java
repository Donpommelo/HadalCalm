package com.mygdx.hadal.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.Array;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.input.PlayerAction;
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.server.User;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.text.HText;

import static com.mygdx.hadal.utils.Constants.INTP_FASTSLOW;
import static com.mygdx.hadal.utils.Constants.TRANSITION_DURATION;

/**
 * The UISpectator is used by spectators to view the game. It features the ability to cycle through spectate targets
 * as well dragging the free camera.
 * @author Diphelia Dorlov
 */
public class UISpectator extends AHadalActor {

    protected final PlayState state;

    private static final int mainX = -350;
    private static final int mainY = 0;
    private static final int mainXEnabled = 0;
    private static final int mainYEnabled = 0;

    private static final int textX = 15;
    private static final int titleY = 110;
    private static final int instructions1Y = 88;
    private static final int instructions2Y = 66;
    private static final int joinY = 44;
    private static final int toggleY = 22;

    private static final int windowWidth = 350;
    private static final int windowHeight = 125;

    private static final float fontScaleMedium = 0.25f;

    private boolean enabled = true;

    public UISpectator(PlayState state) {
        super(mainXEnabled, mainYEnabled);
        this.state = state;
    }

    @Override
    public void draw(Batch batch, float alpha) {

        if (!state.isSpectatorMode()) { return; }

        GameStateManager.getSimplePatch().draw(batch, getX(), getY(), windowWidth, windowHeight);

        HadalGame.FONT_UI.getData().setScale(fontScaleMedium);

        //display different text if spectating a target or using free-cam, + info about spectator controls
        if (freeCam) {
            HadalGame.FONT_UI.draw(batch, HText.SPECTATING_FREECAM.text(), getX() + textX, titleY);
        } else {
            if (spectatorTarget != null) {
                HadalGame.FONT_UI.draw(batch, HText.SPECTATING.text(spectatorTarget.getName()), getX() + textX, titleY);
            } else {
                HadalGame.FONT_UI.draw(batch, HText.SPECTATING_NA.text(), getX() + textX, titleY);
            }
        }
        HadalGame.FONT_UI.draw(batch, HText.SPECTATING_LMB.text(), getX() + textX, instructions1Y);
        HadalGame.FONT_UI.draw(batch, HText.SPECTATING_RMB.text(), getX() + textX, instructions2Y);

        //display info about rejoining (if applicable). Host gets extra info about selecting levels as spectator
        if (state.getMode().isHub()) {
            if (state.isServer()) {
                HadalGame.FONT_UI.draw(batch, HText.JOIN_OPTION_HOST.text(PlayerAction.PAUSE.getKeyText(), PlayerAction.INTERACT.getKeyText()),
                        getX() + textX, joinY);
            } else {
                HadalGame.FONT_UI.draw(batch, HText.JOIN_OPTION.text(PlayerAction.PAUSE.getKeyText()),
                        getX() + textX, joinY);
            }
        } else {
            HadalGame.FONT_UI.draw(batch, HText.JOIN_CANT.text(), getX() + textX, joinY);
        }
        HadalGame.FONT_UI.draw(batch, HText.TOGGLE.text(PlayerAction.ACTIVE_ITEM.getKeyText()), getX() + textX, toggleY);
    }

    //is lmb held? Used to control camera dragging in free-cam mode
    private boolean mouseHeld;

    //are we in free cam mode?
    private boolean freeCam = true;

    //has any input been given yet? used to start off pointing at a player instead of in free-cam mode
    private boolean firstInputGiven = false;

    //control camera drag. Multiplier for making dragging a bit faster
    private final Vector2 lastMousePosition = new Vector2();
    private final Vector2 mousePosition = new Vector2();
    private final static float dragMultiplier = 2.5f;

    //the player we are spectating, their connID and their user
    private int targetId;
    private Player spectatorTarget;
    private User spectatorUser;
    /**
     * This is run when the the camera updates when dragged
     * @param target: the target of the spectator camera
     */
    public void spectatorDragCamera(Vector2 target) {

        //this makes the camera start off following a player
        if (!firstInputGiven) {
            findValidSpectatorTarget();
        }

        //when lmb is held, we drag the camera by a factor of the mouse displacement
        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
            firstInputGiven = true;
            mousePosition.set(Gdx.input.getX(), -Gdx.input.getY());
            if (mouseHeld) {
                target.add(lastMousePosition.sub(mousePosition).scl(dragMultiplier));
            }
            mouseHeld = true;
            freeCam = true;
            spectatorTarget = null;
            lastMousePosition.set(mousePosition);
        } else {
            mouseHeld = false;
        }

        //if we are spectating another player, the camera moves towards their location (if they have a body)
        if (!freeCam && spectatorUser != null) {
            if (spectatorTarget.getBody() != null && spectatorTarget.isAlive()) {
                target.set(spectatorTarget.getPixelPosition());
            } else if (!spectatorUser.isSpectator() && spectatorUser.getPlayer() != null) {
                spectatorTarget = spectatorUser.getPlayer();
            } else {
                freeCam = false;
            }
        }
    }

    private final Array<User> users = new Array<>();
    /**
     * This searches for a target to spectatr
     */
    public void findValidSpectatorTarget() {

        User currentUser;

        users.clear();
        if (state.isServer()) {
            users.addAll(HadalGame.server.getUsers().values().toArray());
            currentUser = HadalGame.server.getUsers().get(targetId);
        } else {
            users.addAll(HadalGame.client.getUsers().values().toArray());
            currentUser = HadalGame.client.getUsers().get(targetId);
        }

        boolean foundTarget = false;

        //if currentUser is null, it means our spectator target is no longer present
        if (currentUser == null) {
            foundTarget = loopThroughUsers(0);
        } else {

            //if we are cycling to the next target to spectate, we iterate through users
            for (int i = 0; i < users.size; i++) {
                if (users.get(i).equals(currentUser)) {

                    //freecam means we just switched to directed camera and should start from index 0
                    if (freeCam) {
                        foundTarget = loopThroughUsers(i);
                    } else {

                        //otherwise, we cycle to the next player in the list
                        foundTarget = loopThroughUsers(i + 1);
                    }
                    break;
                }
            }
        }
        freeCam = !foundTarget;

        if (foundTarget) {
            firstInputGiven = true;
        }
    }

    /**
     * This loops through the user list for a valid spectate target
     * @param startIndex: the first user to check
     * @return if we found a valid target or not
     */
    private boolean loopThroughUsers(int startIndex) {

        //iterate through the users starting at startIndex and wrapping when we hit the end
        for (int i = 0; i < users.size; i++) {
            User nextUser = users.get((i + startIndex) % users.size);

            //when we hit a user with a valid player (i.e not a spectator), we start spectating them
            if (nextUser.getPlayer() != null) {
                if (nextUser.getPlayer().getBody() != null && nextUser.getPlayer().isAlive()) {
                    spectatorUser = nextUser;
                    spectatorTarget = nextUser.getPlayer();
                    targetId = spectatorTarget.getConnId();

                    //sync ui so it shows info for your spectating target
                    state.getUiExtra().syncUIText(UITag.uiType.ALL);
                    return true;
                }
            }
        }
        return false;
    }

    public void toggleSpectatorUI() {
        if (enabled) {
            addAction(Actions.moveTo(mainX, mainY, TRANSITION_DURATION, INTP_FASTSLOW));
        } else {
            addAction(Actions.moveTo(mainXEnabled, mainYEnabled, TRANSITION_DURATION, INTP_FASTSLOW));
        }
        enabled = !enabled;
    }

    public void enableSpectatorUI() {
        addAction(Actions.moveTo(mainXEnabled, mainYEnabled, TRANSITION_DURATION, INTP_FASTSLOW));
        enabled = true;
    }

    public Player getSpectatorTarget() { return spectatorTarget; }
}
