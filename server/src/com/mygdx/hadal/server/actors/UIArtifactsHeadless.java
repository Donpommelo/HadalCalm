package com.mygdx.hadal.server.actors;

import com.mygdx.hadal.actors.UIArtifacts;
import com.mygdx.hadal.states.PlayState;

public class UIArtifactsHeadless extends UIArtifacts {

    public UIArtifactsHeadless(PlayState state) {
        super(state);
    }

    @Override
    public void syncArtifact() {}
}
