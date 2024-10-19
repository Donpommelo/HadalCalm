package com.mygdx.hadal.managers;

import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.map.GameMode;
import com.mygdx.hadal.save.UnlockLevel;
import com.mygdx.hadal.states.*;
import com.mygdx.hadal.users.Transition;
import com.mygdx.hadal.users.User;

import java.util.Objects;

import static com.mygdx.hadal.users.Transition.DEFAULT_FADE_OUT_SPEED;

public class TransitionManager {

    private final PlayState state;

    //if we are transitioning to another state, this is that state
    protected TransitionState nextState;

    //If we are transitioning to another level, this is that level.
    private UnlockLevel nextLevel;
    private GameMode nextMode;
    private String nextStartID;

    public TransitionManager(PlayState state) {
        this.state = state;
    }

    /**
     * This is called when ending a playstate by winning, losing or moving to a new playstate
     */
    public void transitionState() {
        switch (nextState) {
            case RESPAWN:
                FadeManager.fadeIn();
                state.getSpectatorManager().setSpectatorMode(false);

                //Make nextState null so we can transition again
                nextState = null;
                break;
            case RESULTS:

                //create snapshot to use for transition to results state
                FrameBuffer fbo = state.getEndgameManager().resultsStateFreeze(state.getBatch());

                //get a results screen
                StateManager.removeState(SettingState.class, false);
                StateManager.removeState(AboutState.class, false);
                StateManager.removeState(PauseState.class, false);
                StateManager.removeState(PlayState.class, false);
                StateManager.removeState(ClientState.class, false);

                ResultsState resultsState = new ResultsState(state.getApp(), state.getEndgameManager().getResultsText(), state, fbo);
                StateManager.addState(resultsState, LobbyState.class);
                StateManager.addState(resultsState, TitleState.class);
                break;
            case SPECTATOR:
                //When ded but other players alive, spectate a player
                FadeManager.fadeIn();
                state.getSpectatorManager().setSpectatorMode();

                //sometimes, the client can miss the server's delete packet. if so, delete own player automatically
                if (HadalGame.usm.getOwnPlayer() != null && state instanceof ClientState clientState) {
                    if (HadalGame.usm.getOwnPlayer().isAlive()) {
                        clientState.removeEntity(HadalGame.usm.getOwnPlayer().getEntityID());
                    }
                }

                //Make nextState null so we can transition again
                nextState = null;
                break;
            case NEWLEVEL:

                //remove this state and add a new play state with a fresh loadout
                StateManager.removeState(SettingState.class, false);
                StateManager.removeState(AboutState.class, false);
                StateManager.removeState(PauseState.class, false);

                if (state.isServer()) {
                    StateManager.removeState(PlayState.class, false);
                    StateManager.addPlayState(state.getApp(), nextLevel, nextMode, LobbyState.class, true, nextStartID);
                    StateManager.addPlayState(state.getApp(), nextLevel, nextMode, TitleState.class, true, nextStartID);
                }
                break;
            case NEXTSTAGE:
                //remove this state and add a new play state with the player's current loadout and stats
                StateManager.removeState(SettingState.class, false);
                StateManager.removeState(AboutState.class, false);
                StateManager.removeState(PauseState.class, false);

                if (state.isServer()) {
                    StateManager.removeState(PlayState.class, false);
                    StateManager.addPlayState(state.getApp(), nextLevel, nextMode, LobbyState.class, false, nextStartID);
                    StateManager.addPlayState(state.getApp(), nextLevel, nextMode, TitleState.class, false, nextStartID);
                }
                break;
            case TITLE:
                StateManager.removeState(ResultsState.class);
                StateManager.removeState(SettingState.class, false);
                StateManager.removeState(AboutState.class, false);
                StateManager.removeState(PauseState.class, false);
                StateManager.removeState(PlayState.class);
                StateManager.removeState(ClientState.class);

                //add a notification to the title state if specified in transition state
                if (!StateManager.states.isEmpty()) {
                    if (StateManager.states.peek() instanceof TitleState titleState) {
                        titleState.setNotification(state.getEndgameManager().getResultsText());
                    }
                    if (StateManager.states.peek() instanceof LobbyState lobbyState) {
                        lobbyState.setNotification(state.getEndgameManager().getResultsText());
                    }
                }
                break;
            default:
                break;
        }
    }

    /**
     * transition from one playstate to another with a new level.
     * @param level: level of the new map
     * @param mode: mode of the new map
     * @param transitionState: this will either be a new level or next stage to determine whether we reset hp
     * @param nextStartID: The id of the start point to start at (if specified)
     */
    public void loadLevel(UnlockLevel level, GameMode mode, TransitionState transitionState, String nextStartID) {
        //The client should never run this; instead transitioning when the server tells it to.
        if (!state.isServer()) { return; }

        if (nextState == null) {

            //begin transitioning to the designated next level and tell all clients to start transitioning
            nextLevel = level;
            nextMode = mode;
            this.nextStartID = nextStartID;

            for (User user : HadalGame.usm.getUsers().values()) {
                user.getTransitionManager().beginTransition(state, new Transition().setNextState(transitionState)
                        .setReset(transitionState == TransitionState.NEWLEVEL));
            }
        }
    }

    public void loadLevel(UnlockLevel level, TransitionState transitionState, String nextStartID) {
        loadLevel(level, level.getModes()[0], transitionState, nextStartID);
    }

    /**
     * This is called whenever we transition to a new state. Begin transition and set new state.
     * @param transitionState: The state we are transitioning towards
     * @param fadeSpeed: speed of transition
     * @param fadeDelay: amount of delay before transition
     */
    public void beginTransition(TransitionState transitionState, float fadeSpeed, float fadeDelay, boolean skipFade) {
        //If we are already transitioning to a new results state, do not do this unless we tell it to override
        if (!skipFade) {
            FadeManager.fadeSpecificSpeed(fadeSpeed, fadeDelay);
            FadeManager.setRunAfterTransition(this::transitionState);

            //null nextState is used by user transition for non-timed respawn
            nextState = Objects.requireNonNullElse(transitionState, TransitionState.RESPAWN);
        } else {
            state.getSpectatorManager().setSpectatorMode(false);
            nextState = null;
        }

        //fadeSpeed = 0 means we skip the fade. Only relevant during special transitions
        if (TransitionState.RESPAWN.equals(transitionState) && !skipFade && fadeDelay != 0.0f) {
            state.getUIManager().getKillFeed().addKillInfo(fadeDelay + 1.0f / fadeSpeed);
        }
    }

    /**
     * Return to the title screen after a disconnect or selecting return in the pause menu. Overrides other transitions.
     */
    public void returnToTitle(float delay) {
        if (state.isServer()) {
            if (HadalGame.server.getServer() != null) {
                HadalGame.server.getServer().stop();
            }
        } else {
            HadalGame.client.getClient().stop();
        }

        beginTransition(TransitionState.TITLE, DEFAULT_FADE_OUT_SPEED, delay, false);
    }

    public void setNextState(TransitionState nextState) { this.nextState = nextState; }

    public TransitionState getNextState() { return nextState; }

    public void setNextLevel(UnlockLevel nextLevel) { this.nextLevel = nextLevel; }

    public void setNextMode(GameMode nextMode) { this.nextMode = nextMode; }

    public enum TransitionState {
        RESPAWN,
        RESULTS,
        SPECTATOR,
        NEWLEVEL,
        NEXTSTAGE,
        TITLE
    }
}
