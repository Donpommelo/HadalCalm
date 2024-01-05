package com.mygdx.hadal.users;

import com.mygdx.hadal.states.PlayState;

public class PlayerTransition {

    private final User user;
    private PlayState.TransitionState nextState;
    private float transitionTime, forewarnTime;
    private boolean spawnForewarned;

    public PlayerTransition(User user) {
        this.user = user;
    }

    public void controller(float delta) {

    }

    public PlayerTransition setTransitionTime(float transitionTime) {
        this.transitionTime = transitionTime;
        return this;
    }

    public PlayerTransition setForewarnTime(float forewarnTime) {
        this.forewarnTime = forewarnTime;
        return this;
    }
}
