package com.mygdx.hadal.managers;

import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.actors.DialogBox;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.managers.TransitionManager.TransitionState;
import com.mygdx.hadal.server.AlignmentFilter;
import com.mygdx.hadal.server.packets.Packets;
import com.mygdx.hadal.server.util.PacketManager;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.text.UIText;
import com.mygdx.hadal.users.Transition;
import com.mygdx.hadal.users.User;

import static com.mygdx.hadal.users.Transition.DEFAULT_FADE_OUT_SPEED;
import static com.mygdx.hadal.users.Transition.SHORT_FADE_DELAY;

/**
 * SpectatorManager handles players entering/exiting spectator mode
 */
public class SpectatorManager {

    private final PlayState state;

    //are we currently a spectator or not?
    protected boolean spectatorMode;

    public SpectatorManager(PlayState state) {
        this.state = state;
    }

    /**
     * This is used to make a specific player a spectator after a transition.
     * This is only run by the server
     */
    public void becomeSpectator(User user, boolean notification) {
        if (!user.isSpectator()) {
            if (notification) {
                HadalGame.server.addNotificationToAll(state,"", UIText.SPECTATOR_ENTER.text(user.getStringManager().getName()),
                        true, DialogBox.DialogType.SYSTEM);
            }

            startSpectator(user);

            //we die last so that the on-death transition does not occur (As it will not override the spectator transition unless it is a results screen.)
            if (null != user.getPlayer()) {
                if (null != user.getPlayer().getPlayerData()) {
                    user.getPlayer().getPlayerData().die(state.getWorldDummy().getBodyData(), DamageSource.DISCONNECT);
                }
            }
        }
    }

    /**
     * Make a specific player a spectator. Run by server only
     * @param user: the user to become a spectator
     */
    public void startSpectator(User user) {
        user.getTransitionManager().beginTransition(state,
                new Transition()
                        .setNextState(TransitionManager.TransitionState.SPECTATOR)
                        .setFadeDelay(SHORT_FADE_DELAY));
        PacketManager.serverTCP(user.getConnID(),
                new Packets.ClientStartTransition(TransitionState.SPECTATOR, DEFAULT_FADE_OUT_SPEED, SHORT_FADE_DELAY,
                        false, null));

        //set the spectator's player number to default so they don't take up a player slot
        user.getHitboxFilter().setUsed(false);
        user.setHitboxFilter(AlignmentFilter.NONE);
        user.setSpectator(true);
    }

    /**
     * This is used to make a specific spectator a player after a transition.
     * This is only run by the server
     */
    public void exitSpectator(User user) {
        if (user != null) {
            if (user.isSpectator()) {
                //cannot exit spectator if server is full
                if (HadalGame.usm.getNumPlayers() >= JSONManager.setting.getMaxPlayers() + 1) {
                    HadalGame.server.sendNotification(user.getConnID(), "", UIText.SERVER_FULL.text(), true, DialogBox.DialogType.SYSTEM);
                    return;
                }

                HadalGame.server.addNotificationToAll(state, "", UIText.SPECTATOR_EXIT.text(user.getStringManager().getNameShort()),
                        true, DialogBox.DialogType.SYSTEM);

                //give the new player a player slot
                user.setHitboxFilter(AlignmentFilter.getUnusedAlignment());
                user.setSpectator(false);

                //for host, start transition. otherwise, send transition packet
                user.getTransitionManager().beginTransition(state,
                        new Transition()
                                .setNextState(TransitionState.RESPAWN)
                                .setFadeDelay(SHORT_FADE_DELAY));
            }
        }
    }

    /**
     * Player enters spectator mode. Set up spectator camera and camera bounds
     */
    public void setSpectatorMode() {
        spectatorMode = true;
        state.getUIManager().getUiSpectator().enableSpectatorUI();

        state.getCameraManager().setSpectator();

        //this makes the player's artifacts disappear as a spectator
        state.getUIManager().getUiArtifact().syncArtifact();
    }

    public void setSpectatorMode(boolean spectatorMode) { this.spectatorMode = spectatorMode; }

    public boolean isSpectatorMode() { return spectatorMode; }

}
