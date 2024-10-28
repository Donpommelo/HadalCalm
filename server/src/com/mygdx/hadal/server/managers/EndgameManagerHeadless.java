package com.mygdx.hadal.server.managers;

import com.mygdx.hadal.managers.EndgameManager;
import com.mygdx.hadal.managers.TransitionManager;
import com.mygdx.hadal.states.PlayState;

public class EndgameManagerHeadless extends EndgameManager {

    public EndgameManagerHeadless(PlayState state) {
        super(state);
    }

    @Override
    public void transitionToResultsState(String resultsText, float fadeDelay) {
        super.transitionToResultsState(resultsText, fadeDelay);
        state.getTransitionManager().beginTransition(TransitionManager.TransitionState.RESULTS,0.0f, fadeDelay, false);
    }
}
