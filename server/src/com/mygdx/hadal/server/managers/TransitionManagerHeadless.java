package com.mygdx.hadal.server.managers;

import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.managers.FadeManager;
import com.mygdx.hadal.managers.StateManager;
import com.mygdx.hadal.managers.TransitionManager;
import com.mygdx.hadal.map.GameMode;
import com.mygdx.hadal.save.UnlockLevel;
import com.mygdx.hadal.server.states.PlayStateHeadless;
import com.mygdx.hadal.server.states.ResultsStateHeadless;
import com.mygdx.hadal.states.*;
import com.mygdx.hadal.users.Transition;
import com.mygdx.hadal.users.User;

import java.util.Objects;

public class TransitionManagerHeadless extends TransitionManager {

    public TransitionManagerHeadless(PlayState state) {
        super(state);
    }

    public void transitionState() {
        switch (nextState) {
            case NEWLEVEL:
                //remove this state and add a new play state with a fresh loadout
                StateManager.removeState(PauseState.class, false);
                StateManager.removeState(PlayStateHeadless.class, false);
                StateManager.states.push(new PlayStateHeadless(state.getApp(), nextLevel, nextMode, true, nextStartID));
                StateManager.states.peek().show();
                break;
            case NEXTSTAGE:
                //remove this state and add a new play state with a fresh loadout
                StateManager.removeState(PauseState.class, false);
                StateManager.removeState(PlayStateHeadless.class, false);
                StateManager.states.push(new PlayStateHeadless(state.getApp(), nextLevel, nextMode, false, nextStartID));
                StateManager.states.peek().show();
                break;
            case RESULTS:
                StateManager.removeState(PauseState.class, false);
                StateManager.removeState(PlayStateHeadless.class, false);

                StateManager.states.push(new ResultsStateHeadless(state.getApp(), state));
                StateManager.states.peek().show();
                break;
        }
    }

    @Override
    public void loadLevel(UnlockLevel level, GameMode mode, TransitionState transitionState, String nextStartID) {
        if (nextState == null) {

            //begin transitioning to the designated next level and tell all clients to start transitioning
            nextLevel = level;
            nextMode = mode;
            this.nextStartID = nextStartID;

            Transition transition = new Transition().setNextState(transitionState).setReset(transitionState == TransitionState.NEWLEVEL);
            for (User user : HadalGame.usm.getUsers().values()) {
                user.getTransitionManager().beginTransition(state, transition);
            }

            if (state.getTransitionManager().getNextState() == null) {
                state.getTransitionManager().beginTransition(transitionState, transition.getFadeSpeed(), transition.getFadeDelay(), transition.isSkipFade());
            }
        }
    }

    @Override
    public void beginTransition(TransitionState transitionState, float fadeSpeed, float fadeDelay, boolean skipFade) {
        FadeManager.fadeSpecificSpeed(fadeSpeed, fadeDelay);
        FadeManager.setRunAfterTransition(this::transitionState);

        //null nextState is used by user transition for non-timed respawn
        nextState = Objects.requireNonNullElse(transitionState, TransitionState.RESPAWN);
    }
}
